package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.ProtocolParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.EOFException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Represents a client session in the network server. */
public class Session implements Runnable {
    private SessionId id;
    private Thread thread;
    private TransportLayer transport;
    private Logger logger;
    private Boolean running;
    private EventBus eventBus;

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
    }

    /**
     * Returns the ID of this session.
     *
     * @return the session ID
     */
    public SessionId getId() {
        return this.id;
    }

    /** Starts the session thread. */
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

    /** Runs the session loop, reading from the transport. */
    @Override
    public void run() {
        while (running) {
            try {
                RawPacket rawPacket = transport.read();
                logger.debug("Recieved: {}", rawPacket);

                PrimitiveRequest primitiveRequest = ProtocolParser.parse(rawPacket);
                logger.debug("Parsed request to {}", primitiveRequest);
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
}
