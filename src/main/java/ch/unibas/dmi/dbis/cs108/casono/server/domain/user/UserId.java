package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import java.util.UUID;

/** Represents a unique identifier for a user. */
public class UserId {
    private final UUID value;

    /** Creates a new UserId with a randomly generated UUID. */
    public UserId() {
        this.value = UUID.randomUUID();
    }

    /**
     * Creates a new UserId with the specified UUID.
     *
     * @param value the UUID to use for this UserId
     */
    public UserId(UUID value) {
        this.value = value;
    }

    /**
     * Returns the UUID value of this UserId.
     *
     * @return the UUID value
     */
    public UUID value() {
        return value;
    }
}
