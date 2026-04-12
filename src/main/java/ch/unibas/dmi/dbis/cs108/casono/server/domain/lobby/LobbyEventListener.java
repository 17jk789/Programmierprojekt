package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

/** Listener interface for lobby lifecycle events. */
public interface LobbyEventListener {
    /** Called when a game is automatically started in the given lobby. */
    void onGameStarted(LobbyId lobbyId);
}
