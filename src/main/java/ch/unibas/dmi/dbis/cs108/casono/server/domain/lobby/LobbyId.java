package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

/** Typesafe wrapper for lobby IDs (1-8). */
public record LobbyId(int value) {
    public static LobbyId of(int value) {
        return new LobbyId(value);
    }
}
