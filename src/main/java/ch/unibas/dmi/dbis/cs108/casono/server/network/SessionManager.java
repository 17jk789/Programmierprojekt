package ch.unibas.dmi.dbis.cs108.casono.server.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private Map<SessionId, Session> sessions;

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public void addSession(Session session) {
        sessions.put(session.getId(), session);
        System.out.println("Added session " + session.getId().value() + " to session manager");
    }

    public Session removeSession(SessionId id) {
        System.out.println("Removed session " + id.value() + " from session manager");
        return sessions.remove(id);
    }

    public Session removeSession(Session session) {
        return sessions.remove(session.getId());
    }

    public Session getSessionById(SessionId id) {
        return sessions.get(id);
    }
}
