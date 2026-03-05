package ch.unibas.dmi.dbis.cs108.casono.common.network;

import java.util.UUID;

public class SessionId {
    /**
     * The SessionId is used to identify a unique client connection in the SessionRegistry
     */
    private final UUID value;

    public SessionId() {
        this.value = UUID.randomUUID();
    }

    public SessionId(UUID value) {
        this.value = value;
    }

    public UUID value() {
        return value;
    }
}
