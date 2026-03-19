package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.IOException;

/** Represents a client session in the network server. */
public class Session {
    private SessionId id;
    private TransportLayer transport;

    /**
     * Creates a new Session with the given transport and event bus.
     *
     * @param transport the transport layer for communication
     * @param eventBus the event bus for publishing events
     * @throws IOException if an I/O error occurs during initialization
     */
    public Session(TransportLayer transport, EventBus eventBus) {
        this.id = new SessionId();
        this.transport = transport;
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
     * Returns the TransportLayer of this session
     *
     * @return the transport layer of the session
     */
    public TransportLayer getTransport() {
        return transport;
    }
}
