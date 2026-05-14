package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby.AddResult;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    private final Map<LobbyId, Instant> creationTimes = new ConcurrentHashMap<>();
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
                creationTimes.put(id, Instant.now());
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

        // Check if player was absent and try to restore
        if (lobby.getAbsentPlayers().contains(username)) {
            boolean restored = lobby.restoreAbsentPlayer(username);
            if (restored) {
                LOGGER.info(
                        () ->
                                "User '"
                                        + username
                                        + "' rejoined absent slot in lobby "
                                        + lobbyId.value());
                playerToLobby.put(username, lobbyId);
                return true;
            }
        }

        AddResult result =
                lobby.addPlayerAndMaybeStart(
                        username, maxPlayersPerLobby, AUTO_START_PLAYERS, DEFAULT_START_CHIPS);
        if (result != AddResult.NOT_ADDED) {
            LOGGER.info(
                    () ->
                            "User '"
                                    + username
                                    + "' joined lobby "
                                    + lobbyId.value()
                                    + " (result="
                                    + result
                                    + ")");
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
                // Set the game-ended callback
                notifySetGameEndedCallback(lobbyId);
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

    private void notifySetGameEndedCallback(LobbyId lobbyId) {
        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby != null) {
            lobby.setOnGameEndedCallback(
                    () -> {
                        notifyGameEnded(lobbyId);
                    });
        }
    }

    private void notifyGameEnded(LobbyId lobbyId) {
        for (LobbyEventListener l : listeners) {
            try {
                l.onGameEnded(lobbyId);
            } catch (RuntimeException e) {
                LOGGER.warning(
                        () ->
                                "Listener threw while handling game-end for lobby "
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

    /**
     * Mark a player as absent (left the lobby during a running game). The player is not removed but
     * moved to the absent set, allowing them to rejoin later without losing their slot.
     *
     * @param username the player to mark as absent
     * @param lobbyId the lobby they're leaving
     * @return true if the player was marked absent successfully
     */
    public boolean leavePlayerFromLobby(String username, LobbyId lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return false;
        }
        boolean left = lobby.leavePlayer(username);
        if (left) {
            LOGGER.info(
                    () ->
                            "User '"
                                    + username
                                    + "' left lobby "
                                    + lobbyId.value()
                                    + " (marked absent)");
            // Keep the playerToLobby mapping intact for rejoin
        }
        return left;
    }

    public Collection<Lobby> getAllLobbies() {
        return activeLobbies.values();
    }

    /** Find all empty lobbies that were created more than the given {@code age} ago. */
    public List<LobbyId> findEmptyLobbiesOlderThan(Duration age) {
        List<LobbyId> result = new ArrayList<>();
        Instant cutoff = Instant.now().minus(age);
        for (Map.Entry<LobbyId, Lobby> e : activeLobbies.entrySet()) {
            LobbyId id = e.getKey();
            Lobby l = e.getValue();
            if (l.getPlayerNames().isEmpty()) {
                Instant created = creationTimes.get(id);
                if (created != null && created.isBefore(cutoff)) {
                    result.add(id);
                }
            }
        }
        return result;
    }

    /** Remove a lobby and clean up internal mappings. */
    public void removeLobby(LobbyId id) {
        Lobby removed = activeLobbies.remove(id);
        creationTimes.remove(id);
        if (removed == null) {
            return;
        }
        for (String username : removed.getPlayerNames()) {
            playerToLobby.remove(username);
        }
        for (String username : removed.getAbsentPlayers()) {
            playerToLobby.remove(username);
        }
    }

    /**
     * Renames a player across lobby mapping, lobby player list and running game ids.
     *
     * @param oldUsername old username
     * @param newUsername new username
     * @return true if rename was applied
     */
    public synchronized boolean renamePlayer(String oldUsername, String newUsername) {
        if (oldUsername == null || newUsername == null) {
            return false;
        }
        if (oldUsername.equals(newUsername)) {
            return true;
        }

        LobbyId lobbyId = playerToLobby.get(oldUsername);
        if (lobbyId == null) {
            return true;
        }
        if (playerToLobby.containsKey(newUsername)) {
            return false;
        }

        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby == null) {
            playerToLobby.remove(oldUsername);
            return true;
        }

        boolean lobbyRenamed = lobby.renamePlayer(oldUsername, newUsername);
        if (!lobbyRenamed) {
            return false;
        }

        if (lobby.getGameController() != null) {
            boolean gameRenamed =
                    lobby.getGameController()
                            .renamePlayer(PlayerId.of(oldUsername), PlayerId.of(newUsername));
            if (!gameRenamed) {
                // Best-effort rollback to keep structures consistent.
                lobby.renamePlayer(newUsername, oldUsername);
                return false;
            }
        }

        playerToLobby.remove(oldUsername);
        playerToLobby.put(newUsername, lobbyId);
        return true;
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
