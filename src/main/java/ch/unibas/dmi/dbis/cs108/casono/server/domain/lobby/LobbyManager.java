package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby.AddResult;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/** Manages dynamic creation of up to 8 lobbies and maps players to lobbies. */
public class LobbyManager {
    private static final int MAX_LOBBIES = 8;
    private static final int DEFAULT_MAX_PLAYERS = 4;
    private static final int AUTO_START_PLAYERS = 4;
    private static final int DEFAULT_START_CHIPS = 20000;

    private final Map<LobbyId, Lobby> activeLobbies = new ConcurrentHashMap<>();
    private final Map<String, LobbyId> playerToLobby = new ConcurrentHashMap<>();
    private final List<LobbyEventListener> listeners = new CopyOnWriteArrayList<>();
    private final int maxPlayersPerLobby;
    private static final Logger LOGGER = Logger.getLogger(LobbyManager.class.getName());

    public LobbyManager() {
        this(DEFAULT_MAX_PLAYERS);
    }

    public LobbyManager(int maxPlayersPerLobby) {
        this.maxPlayersPerLobby = maxPlayersPerLobby;
    }

    /** Create a new lobby with an automatic id (1..8). Returns null if none available. */
    public synchronized LobbyId createNewLobby(String name) {
        if (activeLobbies.size() >= MAX_LOBBIES) {
            return null;
        }
        for (int i = 1; i <= MAX_LOBBIES; i++) {
            LobbyId id = LobbyId.of(i);
            if (!activeLobbies.containsKey(id)) {
                Lobby lobby = new Lobby(id, name == null ? ("Room " + i) : name);
                activeLobbies.put(id, lobby);
                return id;
            }
        }
        return null;
    }

    public Lobby getLobby(LobbyId id) {
        if (id == null) {
            return null;
        }
        return activeLobbies.get(id);
    }

    public Lobby getLobbyByUsername(String username) {
        LobbyId id = playerToLobby.get(username);
        if (id == null) {
            return null;
        }
        return activeLobbies.get(id);
    }

    public boolean addPlayerToLobby(String username, LobbyId lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return false;
        }
        AddResult result =
                lobby.addPlayerAndMaybeStart(
                        username, maxPlayersPerLobby, AUTO_START_PLAYERS, DEFAULT_START_CHIPS);
        if (result != AddResult.NOT_ADDED) {
            playerToLobby.put(username, lobbyId);
            if (result == AddResult.ADDED_AND_STARTED) {
                LOGGER.info(
                        () ->
                                "Lobby "
                                        + lobbyId.value()
                                        + " reached "
                                        + AUTO_START_PLAYERS
                                        + " players; game started.");
                notifyGameStarted(lobbyId);
            }
            return true;
        }
        return false;
    }

    public void addListener(LobbyEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LobbyEventListener listener) {
        listeners.remove(listener);
    }

    private void notifyGameStarted(LobbyId lobbyId) {
        for (LobbyEventListener l : listeners) {
            try {
                l.onGameStarted(lobbyId);
            } catch (RuntimeException e) {
                LOGGER.warning(
                        () ->
                                "Listener threw while handling game-start for lobby "
                                        + lobbyId.value());
            }
        }
    }

    public boolean removePlayer(String username) {
        LobbyId id = playerToLobby.remove(username);
        if (id == null) {
            return false;
        }
        Lobby lobby = activeLobbies.get(id);
        if (lobby == null) {
            return false;
        }
        boolean removed = lobby.removePlayer(username);
        // If lobby becomes empty, remove it
        if (lobby.getPlayerNames().isEmpty()) {
            activeLobbies.remove(id);
        }
        return removed;
    }

    public Collection<Lobby> getAllLobbies() {
        return activeLobbies.values();
    }

    /**
     * Apply the given action to every player username in the lobby identified by {@code lobbyId}.
     * This is a small helper that keeps iteration logic centralized and avoids leaking internal
     * collections to callers.
     */
    public void broadcast(LobbyId lobbyId, java.util.function.Consumer<String> action) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return;
        }
        for (String username : lobby.getPlayerNames()) {
            action.accept(username);
        }
    }
}
