package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import ch.unibas.dmi.dbis.cs108.casono.server.network.handlers.ChatHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;

/**
 * Represents a client session in the network server.
 */
public class Session implements Runnable {
    private final ChatHandler chatHandler;
    private SessionId id;
    private Thread thread;
    private TransportLayer transport;
    private Logger logger;
    private Boolean running;
    private EventBus eventBus;
    private AtomicInteger idGenerator;

    /**
     * Creates a new Session with the given transport and event bus.
     *
     * @param transport the transport layer for communication
     * @param eventBus the event bus for publishing events
     * @throws IOException if an I/O error occurs during initialization
     */
    public Session(TransportLayer transport, EventBus eventBus) throws IOException {
        this.id = new SessionId();
        this.thread = new Thread(this, "session-" + this.id.value());
        this.transport = transport;
        this.running = true;
        this.eventBus = eventBus;

        this.logger = LogManager.getLogger(Session.class.toString() + id.value());
        this.logger.info("Created new session");
        this.idGenerator = new AtomicInteger();
        this.chatHandler = new ChatHandler(eventBus);
    }

    /**
     * Returns the ID of this session.
     *
     * @return the session ID
     */
    public SessionId getId() {
        return this.id;
    }

    /**
     * Starts the session thread.
     */
    public void start() {
        thread.start();
    }

    /**
     * Closes the session and its transport.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        transport.close();
        this.running = false;
    }

    /**
     * Runs the session loop, reading from the transport.
     */
    @Override
    public void run() {
        while (running) {
            try {
                String clientCommand = transport.read().payload();
                String[] commandAndArgs = clientCommand.split("\\s+", 2);
                String commandWord = commandAndArgs[0];
                System.out.println("Session "+id.value()+" Received: " + commandAndArgs[0]);
            switch (commandWord) {
                    case "SEND_MESSAGE":
                        this.chatHandler.sendMessage(commandAndArgs[1]);
                        writeToTransport("+OK");
                        writeToTransport("+OK");
                        break;
                    case "GET_MESSAGE_COUNT":
                        int count = chatHandler.getMessageCount();
                        writeToTransport(""+count);
                        writeToTransport("+OK");
                        break;
                    case "GET_NEXT_MESSAGE":
                         if (chatHandler.getMessageCount() == 0) {
                             logger.warn("FAIL No more messages!");
                             writeToTransport("-FAIL");
                         } else {
                             String msgString = chatHandler.getNextMessage().toArgsString();
                             logger.debug("Session "+id.value()+" Write next message "+msgString);
                             writeToTransport(msgString);
                             writeToTransport("+OK");
                         }
                         break;
                    default:
                        this.logger.warn("Unknown command: " + commandWord);
                        writeToTransport("-FAIL");
                }
            } catch (EOFException e) {
                logger.info("Client disconnected");
                eventBus.publish(new DisconnectEvent(id));
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void writeToTransport(String s) throws IOException {
        int id = this.idGenerator.incrementAndGet();
        this.transport.write(new RawPacket(id, s));
    }
}
