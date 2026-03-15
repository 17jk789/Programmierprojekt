package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Manages all active users on the server. */
public class UserRegistry {
    private final ConcurrentHashMap<UserId, User> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> byName = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SessionId, User> bySessionId = new ConcurrentHashMap<>();

    /**
     * Attempts to register a user under the given name atomically. Returns the registered user, or
     * empty if the name is already taken.
     *
     * @param name the desired display name
     * @param sessionId the session to associate with the new user
     * @return an Optional containing the new user, or empty if the name was taken
     */
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

    /**
     * Removes the user with the given ID, but only if they are still disconnected. This prevents
     * removing a user who has reconnected between the cleanup job's check and its removal call.
     *
     * @param userId the ID of the user to remove
     */
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

    /**
     * Marks the user associated with the given session as disconnected, clearing the session
     * association and recording the disconnect timestamp.
     *
     * @param sessionId the session ID of the disconnected client
     */
    // TODO: Add to EventRegistry with DisconnectEvent
    public synchronized void onDisconnect(SessionId sessionId) {
        User user = bySessionId.remove(sessionId);
        if (user == null) {
            return;
        }

        user.markDisconnected();
    }

    /**
     * Reassociates a user with a new session, effectively restoring them after a reconnect.
     *
     * @param userId the ID of the user to reconnect
     * @param sessionId the new session ID
     * @return an Optional containing the user, or empty if the user was not found
     */
    public synchronized Optional<User> reassignSession(UserId userId, SessionId sessionId) {
        User user = byId.get(userId);
        if (user == null) {
            return Optional.empty();
        }

        user.reassignSession(sessionId);
        bySessionId.put(sessionId, user);
        return Optional.of(user);
    }

    /**
     * Looks up a user by their session ID.
     *
     * @param sessionId the session ID to look up
     * @return an Optional containing the user, or empty if no user is associated with this session
     */
    public Optional<User> findBySessionId(SessionId sessionId) {
        return Optional.ofNullable(bySessionId.get(sessionId));
    }

    /**
     * Returns all currently registered users.
     *
     * @return a collection of all users
     */
    public Collection<User> getAllUsers() {
        return byId.values();
    }
}
