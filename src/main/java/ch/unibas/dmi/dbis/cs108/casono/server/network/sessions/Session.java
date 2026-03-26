package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.CommandRouter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.response.PrimitiveResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Represents a client session in the network server. */
public class Session {
    private final SessionId id;
    private final TransportLayer transport;
    private final BlockingQueue<PrimitiveResponse> responseQueue;
    private final CommandParserDispatcher dispatcher;
    private final CommandRouter router;
    private static final int RESPOND_QUEUE_SIZE = 32;

    /**
     * Creates a new Session with the given transport and event bus.
     *
     * @param transport the transport layer for communication
     * @param eventBus the event bus for publishing events
     * @throws IOException if an I/O error occurs during initialization
     */
    public Session(
            TransportLayer transport, EventBus eventBus, CommandParserDispatcher dispatcher, CommandRouter router) {
        this.id = new SessionId();
        this.transport = transport;
        this.dispatcher = dispatcher;
        this.router = router;
        this.responseQueue = new ArrayBlockingQueue<>(RESPOND_QUEUE_SIZE);
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

    /**
     * Returns the BlockingQueue of this session
     *
     * @return the queue containing outgoing responses
     */
    public BlockingQueue<PrimitiveResponse> getResponseQueue() {
        return responseQueue;
    }

    /**
     * Returns the CommandParserDispatcher of this session
     *
     * @return the dispatcher to dispatch PrimitiveRequests to for parsing
     */
    public CommandParserDispatcher getDispatcher() {
        return dispatcher;
    }

    public CommandRouter getRouter() {
        return router;
    }
}
