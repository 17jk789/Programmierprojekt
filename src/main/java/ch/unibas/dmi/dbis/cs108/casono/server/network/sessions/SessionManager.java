package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;

/** Manages active sessions in the server. */
public class SessionManager {
    private Map<SessionId, SessionHandle> sessions;
    private final EventBus eventBus;
    private final Logger logger;

    /** Constructs a new SessionManager. */
    public SessionManager(EventBus eventBus) {
        this.sessions = new ConcurrentHashMap<>();
        this.eventBus = eventBus;
        this.logger = LogManager.getLogger(SessionManager.class);
    }

    /**
     * Create new Session from provided transport.
     * 
     * <p> Will create both worker threads and start them.
     * 
     * @param transport to create session from
     * @return newly created session
     */
    public Session create(TransportLayer transport) {
        Session session = new Session(transport, eventBus);

        SessionReader reader = new SessionReader(session, eventBus);
        Thread readerThread = new Thread(reader, "session-" + session.getId().value() + "-reader");
        readerThread.start();

        SessionWriter writer = new SessionWriter(session);
        Thread writerThread = new Thread(writer, "session-" + session.getId().value() + "-writer");
        writerThread.start();

        SessionHandle handle = new SessionHandle(session, readerThread, writerThread);
        sessions.put(session.getId(), handle);
        logger.debug("Created new session {}", session.getId().value());
        return session;
    }

    /**
     * Disconnect specified client
     * 
     * <p> WARNING: Client will be uninformed about disconnect. Use with caution.
     * 
     * @param id of the client to disconnect
     */
    public void disconnect(SessionId id) {
        SessionHandle handle = sessions.get(id);
        if (handle == null) {
            logger.warn("Requested to disconnect client with id {}. Failed as client is not found", id.value());
            return;
        }
        logger.debug("Disconnecting session {}", id.value());

        handle.reader().interrupt();
        handle.writer().interrupt();
        try {
            handle.session().getTransport().close();
        } catch (IOException e) {
            logger.trace("Unexpected exception while closing transport", e);
        }
    }

    /**
     * Removes a session by its ID.
     *
     * @param id the ID of the session to remove
     * @return the removed session, or null if not found
     */
    public Session removeSession(SessionId id) {
        SessionHandle handle = sessions.remove(id);

        if (handle == null) {
            return null;
        }

        return handle.session();
    }

    /**
     * Removes the specified session.
     *
     * @param session the session to remove
     * @return the removed session, or null if not found
     */
    public Session removeSession(Session session) {
        SessionHandle handle = sessions.remove(session.getId());

        if (handle == null) {
            return null;
        }

        return handle.session();
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
