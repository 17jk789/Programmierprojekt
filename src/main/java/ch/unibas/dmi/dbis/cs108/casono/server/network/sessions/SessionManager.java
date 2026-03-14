package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Manages active sessions in the server. */
public class SessionManager {
    private Map<SessionId, Session> sessions;

    /** Constructs a new SessionManager. */
    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * Adds a session to the manager.
     *
     * @param session the session to add
     */
    public void addSession(Session session) {
        sessions.put(session.getId(), session);
        System.out.println("Added session " + session.getId().value() + " to session manager");
    }

    /**
     * Removes a session by its ID.
     *
     * @param id the ID of the session to remove
     * @return the removed session, or null if not found
     */
    public Session removeSession(SessionId id) {
        System.out.println("Removed session " + id.value() + " from session manager");
        return sessions.remove(id);
    }

    /**
     * Removes the specified session.
     *
     * @param session the session to remove
     * @return the removed session, or null if not found
     */
    public Session removeSession(Session session) {
        return sessions.remove(session.getId());
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param id the ID of the session to retrieve
     * @return the session with the specified ID, or null if not found
     */
    public Session getSessionById(SessionId id) {
        return sessions.get(id);
    }
}
