package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.time.Instant;
import java.util.Optional;

public class User {
    private final UserId id;
    private final String name;
    private SessionId sessionId;
    private Instant disconnectedAt;

    public User(UserId id, String name, SessionId sessionId) {
        this.id = id;
        this.name = name;
        this.sessionId = sessionId;
        this.disconnectedAt = null;
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<SessionId> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public Optional<Instant> getDisconnectedAt() {
        return Optional.ofNullable(disconnectedAt);
    }

    public void reassignSession(SessionId sessionId) {
        this.sessionId = sessionId;
        this.disconnectedAt = null;
    }

    public void markDisconnected() {
        this.sessionId = null;
        this.disconnectedAt = Instant.now();
    }
}
