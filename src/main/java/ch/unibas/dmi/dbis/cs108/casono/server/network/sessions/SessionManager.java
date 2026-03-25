package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Manages active sessions in the server. */
public class SessionManager {
    private Map<SessionId, SessionHandle> sessions;
    private final EventBus eventBus;
    private final Logger logger;
    private final CommandParserDispatcher dispatcher;

    /** Constructs a new SessionManager. */
    public SessionManager(EventBus eventBus, CommandParserDispatcher dispatcher) {
        this.sessions = new ConcurrentHashMap<>();
        this.eventBus = eventBus;
        this.logger = LogManager.getLogger(SessionManager.class);
        this.dispatcher = dispatcher;
    }

    /**
     * Create new Session from provided transport.
     *
     * <p>Will create both worker threads and start them.
     *
     * @param transport to create session from
     * @return newly created session
     */
    public Session create(TransportLayer transport) {
        Session session = new Session(transport, eventBus, dispatcher);
        SessionReader reader = new SessionReader(session, eventBus);
        SessionWriter writer = new SessionWriter(session);

        Thread readerThread = Thread.ofVirtual()
            .name("session-" + session.getId().value() + "-reader")
            .unstarted(reader);
        Thread writerThread = Thread.ofVirtual()
            .name("session-" + session.getId().value() + "-writer")
            .unstarted(writer);

        sessions.put(session.getId(), new SessionHandle(session, readerThread, writerThread));
        readerThread.start();
        writerThread.start();
        return session;
    }

    /**
     * Disconnect specified client
     *
     * <p>WARNING: Client will be uninformed about disconnect. Use with caution.
     *
     * @param id of the client to disconnect
     */
    public void disconnect(SessionId id) {
        SessionHandle handle = sessions.remove(id);
        if (handle == null) {
            logger.warn(
                    "Requested to disconnect client with id {}. Failed as client is not found",
                    id.value());
            return;
        }
        logger.debug("Disconnecting session {}", id.value());

        handle.reader().interrupt();
        handle.writer().interrupt();
        try {
            handle.session().getTransport().close();
        } catch (IOException e) {
            logger.error("Unexpected exception while closing transport", e);
        }
    }

    /**
     * Handler for the DisconnectEvent
     *
     * @param id of the session that disconnected
     */
    public void onDisconnect(DisconnectEvent event) {
        logger.debug("Recieved DisconnectEvent event for session {}", event.sessionId().value());

        disconnect(event.sessionId());
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param id the ID of the session to retrieve
     * @return the session with the specified ID, or null if not found
     */
    public Session getSessionById(SessionId id) {
        SessionHandle handle = sessions.get(id);

        if (handle == null) {
            return null;
        }

        return handle.session();
    }
}
