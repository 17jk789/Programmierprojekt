package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.io.EOFException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;

public class Session implements Runnable {
    private SessionId id;
    private Thread thread;
    private TransportLayer transport;
    private Logger logger;
    private Boolean running;
    private EventBus eventBus;
    
    public Session(TransportLayer transport, EventBus eventBus) throws IOException {
        this.id = new SessionId();
        this.thread = new Thread(this, "session-" + this.id.value());
        this.transport = transport;
        this.running = true;
        this.eventBus = eventBus;

        this.logger = LogManager.getLogger(Session.class.toString() + id.value());
        this.logger.info("Created new session");
    }

    public SessionId getId() {
        return this.id;
    }

    public void start() {
        thread.start();
    }

    public void close() throws IOException {
        transport.close();
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                System.out.println("Recieved: " + transport.read());
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