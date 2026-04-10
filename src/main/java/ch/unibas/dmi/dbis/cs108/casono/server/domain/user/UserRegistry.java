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
     * Attempts to register a user under the given name atomically. Returns the
     * registered user, or
     * empty if the name is already taken.
     *
     * @param name      the desired display name
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
     * Removes a user from the registry by user ID.
     *
     * @param userId the ID of the user to remove
     * @return true if a user was removed, false if no user with that ID exists
     */
    public synchronized boolean removeByUserId(UserId userId) {
        User user = byId.get(userId);
        if (user == null) {
            return false;
        }

        byName.remove(user.getName());
        user.getSessionId().ifPresent(bySessionId::remove);
        return true;
    }

    /**
     * Removes a user from the registry by session ID.
     *
     * @param sessionId the session ID associated with the user to remove
     * @return true if a user was removed, false if no user with that session exists
     */
    public synchronized boolean removeBySessionId(SessionId sessionId) {
        User user = bySessionId.get(sessionId);
        if (user == null) {
            return false;
        }

        remove(user);
        return true;
    }

    /**
     * Removes a user from the registry by username.
     *
     * @param username the username associated with the user to remove
     * @return true if a user was removed, false if no user with that session exists
     */
    public synchronized boolean removeByUsername(String username) {
        User user = byName.get(username);
        if (user == null) {
            return false;
        }

        remove(user);
        return true;
    }

    /**
     * Removes a user from the internals of the registry.
     *
     * @param user the user to remove
     */
    private synchronized void remove(User user) {
        byId.remove(user.getId());
        byName.remove(user.getName());
        user.getSessionId().ifPresent(bySessionId::remove);
    }

    /**
     * Removes the user with the given ID, but only if they are still disconnected.
     * This prevents
     * removing a user who has reconnected between the cleanup job's check and its
     * removal call.
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

        remove(user);
        return true;
    }

    /**
     * Marks the user associated with the given session as disconnected, clearing
     * the session
     * association and recording the disconnect timestamp.
     *
     * @param sessionId the session ID of the disconnected client
     */
    public synchronized void onDisconnect(SessionId sessionId) {
        User user = bySessionId.remove(sessionId);
        if (user == null) {
            return;
        }

        user.markDisconnected();
    }

    /**
     * Reassociates a user with a new session, effectively restoring them after a
     * reconnect.
     *
     * @param userId    the ID of the user to reconnect
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
     * Looks up a user by their {@link SessionId}.
     *
     * @param sessionId the SessionId to look up
     * @return an Optional containing the {@link User}, or empty if no user is
     *         associated with this
     *         SessionId
     */
    public Optional<User> getBySessionId(SessionId sessionId) {
        return Optional.ofNullable(bySessionId.get(sessionId));
    }

    /**
     * Looks up a user by their {@link UserId}.
     *
     * @param userId the UserId to look up
     * @return an Optional containing the {@link User}, or empty if no user is
     *         associated with this
     *         UserId
     */
    public Optional<User> getByUserId(UserId userId) {
        return Optional.ofNullable(byId.get(userId));
    }

    /**
     * Looks up a user by their username.
     *
     * @param username the username to look up
     * @return an Optional containing the {@link User}, or empty if no user is
     *         associated with this
     *         username
     */
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(byName.get(username));
    }

    /**
     * Returns all currently registered users.
     *
     * @return a collection of all users
     */
    public Collection<User> getAllUsers() {
        return byId.values();
    }

    /**
     * Attempts to change the username of an existing user. Ensures the new name is
     * not already
     * taken and updates internal indices atomically.
     *
     * @param userId  the id of the user to rename
     * @param newName the desired new name
     * @return true if the rename succeeded, false if the name was already taken or
     *         user not found
     */
    public synchronized boolean changeUsername(UserId userId, String newName) {
        User user = byId.get(userId);
        if (user == null) {
            return false;
        }

        if (byName.containsKey(newName)) {
            return false;
        }

        // remove old mapping and put new mapping
        byName.remove(user.getName());
        user.setName(newName);
        byName.put(newName, user);
        return true;
    }
}
