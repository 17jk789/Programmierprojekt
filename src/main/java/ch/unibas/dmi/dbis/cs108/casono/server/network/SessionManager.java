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
    }

    public Session removeSession(SessionId id) {
        return sessions.remove(id);
    }

    public Session removeSession(Session session) {
        return sessions.remove(session.getId());
    }

    public Session getSessionById(SessionId id) {
        return sessions.get(id);
    }
}
