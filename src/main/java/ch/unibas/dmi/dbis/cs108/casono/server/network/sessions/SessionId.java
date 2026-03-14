package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import java.util.UUID;

/** Represents a unique identifier for a session. */
public class SessionId {
    private final UUID value;

    /** Creates a new SessionId with a randomly generated UUID. */
    public SessionId() {
        this.value = UUID.randomUUID();
    }

    /**
     * Creates a new SessionId with the specified UUID.
     *
     * @param UUID to use for this SessionId
     */
    public SessionId(UUID value) {
        this.value = value;
    }

    /**
     * Returns the UUID value of this SessionId.
     *
     * @return the UUID value
     */
    public UUID value() {
        return value;
    }
}
