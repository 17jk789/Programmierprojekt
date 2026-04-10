package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.message.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Represents an authenticated user on the server. */
public class User {
    private final UserId id;
    private String name;
    private SessionId sessionId;
    private Instant disconnectedAt;
    private final Queue<Message> messages;

    /**
     * Creates a new User with the given ID, name and session.
     *
     * @param id        the unique identifier for this user
     * @param name      the display name of this user
     * @param sessionId the session currently associated with this user
     */
    public User(UserId id, String name, SessionId sessionId) {
        this.id = id;
        this.name = name;
        this.sessionId = sessionId;
        this.disconnectedAt = null;
        this.messages = new ConcurrentLinkedQueue<>();
    }

    /**
     * Returns the ID of this user.
     *
     * @return the user ID
     */
    public UserId getId() {
        return id;
    }

    /**
     * Returns the display name of this user.
     *
     * @return the user name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets a new display name for this user. Thread-safe; callers must ensure the
     * registry is
     * updated to maintain uniqueness when needed.
     *
     * @param newName the new display name
     */
    public synchronized void setName(String newName) {
        this.name = newName;
    }

    /**
     * Returns the session currently associated with this user, if any.
     *
     * @return an Optional containing the session ID, or empty if disconnected
     */
    public Optional<SessionId> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    /**
     * Returns the time at which this user disconnected, if applicable.
     *
     * @return an Optional containing the disconnect timestamp, or empty if
     *         connected
     */
    public Optional<Instant> getDisconnectedAt() {
        return Optional.ofNullable(disconnectedAt);
    }

    /**
     * Associates this user with a new session, clearing the disconnect timestamp.
     *
     * @param sessionId the new session ID
     */
    public void reassignSession(SessionId sessionId) {
        this.sessionId = sessionId;
        this.disconnectedAt = null;
    }

    /**
     * Marks this user as disconnected by clearing the session and recording the
     * timestamp.
     */
    public void markDisconnected() {
        this.sessionId = null;
        this.disconnectedAt = Instant.now();
    }

    public synchronized void enqueueMessage(Message message) {
        messages.add(message);
    }

    public synchronized List<Message> dequeueAllMessages(Message message) {
        List<Message> allMessages = new ArrayDeque<>(messages).stream().toList();
        messages.clear();
        return allMessages;
    }
}
