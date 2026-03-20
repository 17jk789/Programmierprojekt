package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Manages active sessions in the server. */
public class SessionManager {
    private Map<SessionId, SessionHandle> sessions;

    /** Constructs a new SessionManager. */
    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * Adds a session to the manager.
     *
     * @param session the session to add
     */
    public void addSession(SessionHandle handle) {
        sessions.put(handle.session().getId(), handle);
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
