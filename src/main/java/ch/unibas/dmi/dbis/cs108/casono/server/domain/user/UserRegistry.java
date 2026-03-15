package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private final ConcurrentHashMap<UserId, User> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> byName = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SessionId, User> bySessionId = new ConcurrentHashMap<>();

    public synchronized Optional<User> registerIfAvailable(String name, SessionId sessionId) {
        if (byName.containsKey(name)) {
            return Optional.empty();
        }

        User user = new User(new UserId(), name, sessionId);
        byId.put(user.getId(), user);
        byName.put(user.getName(), user);
        bySessionId.put(sessionId, user);
        return Optional.of(user);
    }

    public synchronized boolean removeIfStillDisconnected(UserId userId) {
        User user = byId.get(userId);
        if (user == null) {
            return false;
        }

        if (user.getSessionId().isPresent()) {
            return false;
        }

        byId.remove(user.getId());
        byName.remove(user.getName());
        return true;
    }

    // TODO: Add to EventRegistry with DisconnectEvent
    public synchronized void onDisconnect(SessionId sessionId) {
        User user = bySessionId.remove(sessionId);
        if (user == null) {
            return;
        }

        user.markDisconnected();
    }

    public synchronized Optional<User> reassignSession(UserId userId, SessionId sessionId) {
        User user = byId.get(userId);
        if (user == null) {
            return Optional.empty();
        }

        user.reassignSession(sessionId);
        bySessionId.put(sessionId, user);
        return Optional.of(user);
    }

    public Optional<User> findBySessionId(SessionId sessionId) {
        return Optional.ofNullable(bySessionId.get(sessionId));
    }

    public Collection<User> getAllUsers() {
        return byId.values();
    }
}
