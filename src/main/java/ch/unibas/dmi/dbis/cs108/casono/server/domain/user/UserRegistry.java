package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private final ConcurrentHashMap<UserId, User> byId = new ConcurrentHashMap<>();

    public synchronized Optional<User> registerIfAvailable(String name, SessionId sessionId) {
        Iterator<User> users = byId.elements().asIterator();
        while(users.hasNext()) {
            User user = users.next();

            if (user.getName() == name) {
                return Optional.empty();
            }
        }

        User user = new User(new UserId(), name, sessionId);
        byId.put(user.getId(), user);
        return Optional.of(user);
    }

    public Optional<User> findBySessionId(SessionId sessionId) {
        Iterator<User> users = byId.elements().asIterator();
        while(users.hasNext()) {
            User user = users.next();
            Optional<SessionId> currentSessionId = user.getSessionId();
            
            if (currentSessionId.isEmpty()) {
                continue;
            }

            if (currentSessionId.get() == sessionId) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // TODO: Add to EventRegistry with DisconnectEvent
    public synchronized void onDisconnect(SessionId sessionId) {
        Optional<User> user = findBySessionId(sessionId);

        if (user.isPresent()) {
            user.get().markDisconnected();
        }
    }

}