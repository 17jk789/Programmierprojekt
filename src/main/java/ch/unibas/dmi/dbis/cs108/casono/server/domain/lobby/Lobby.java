package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.GameController;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.RoundManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.TurnManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Represents a single lobby: id, name, players and an optional GameController. */
public class Lobby {
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());

    public enum AddResult {
        NOT_ADDED,
        ADDED,
        ADDED_AND_STARTED
    }

    private final LobbyId id;
    private final String name;
    private final List<String> playerNames = new CopyOnWriteArrayList<>();
    private final Set<String> absentPlayers = ConcurrentHashMap.newKeySet();
    private volatile GameController gameController;
    private volatile Runnable onGameEndedCallback;

    public Lobby(LobbyId id, String name) {
        this.id = id;
        this.name = name;
    }

    public LobbyId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getPlayerNames() {
        return List.copyOf(playerNames);
    }

    /**
     * Try to add a player to this lobby.
     *
     * @return true if added, false if already present or full
     */
    public boolean addPlayer(String playerName, int maxPlayers) {
        if (playerName == null) {
            return false;
        }
        synchronized (playerNames) {
            if (playerNames.contains(playerName)) {
                return false;
            }
            if (playerNames.size() >= maxPlayers) {
                return false;
            }
            playerNames.add(playerName);
            return true;
        }
    }

    public boolean removePlayer(String playerName) {
        return playerNames.remove(playerName);
    }

    /**
     * Renames a player in this lobby's player list.
     *
     * @param oldName old username
     * @param newName new username
     * @return true if renamed successfully
     */
    public boolean renamePlayer(String oldName, String newName) {
        if (oldName == null || newName == null) {
            return false;
        }
        synchronized (playerNames) {
            if (oldName.equals(newName)) {
                return playerNames.contains(oldName);
            }
            int idx = playerNames.indexOf(oldName);
            if (idx < 0 || playerNames.contains(newName)) {
                return false;
            }
            playerNames.set(idx, newName);
            return true;
        }
    }

    public void initGame(GameController controller) {
        this.gameController = controller;
        if (controller != null && onGameEndedCallback != null) {
            controller.setOnGameEndedCallback(onGameEndedCallback);
        }
    }

    /**
     * Set a callback to be invoked when the game in this lobby ends. This will be propagated to the
     * game controller when a game is started.
     */
    public void setOnGameEndedCallback(Runnable callback) {
        this.onGameEndedCallback = callback;
        if (gameController != null) {
            gameController.setOnGameEndedCallback(callback);
        }
    }

    public GameController getGameController() {
        return gameController;
    }

    /**
     * Check if a player is currently active (not absent) in the lobby.
     *
     * @param playerName the player to check
     * @return true if player is in the active player list
     */
    public boolean isPlayerActive(String playerName) {
        return playerNames.contains(playerName);
    }

    /**
     * Mark a player as absent (left the lobby) while the game is still running. The player remains
     * in the lobby's mappings but is moved to the absent set.
     *
     * @param playerName the player to mark as absent
     * @return true if the player was active and is now absent
     */
    public boolean leavePlayer(String playerName) {
        if (playerName == null) {
            return false;
        }
        boolean removed = playerNames.remove(playerName);
        if (removed) {
            absentPlayers.add(playerName);
        }
        return removed;
    }

    /**
     * Try to restore an absent player back to active. Returns true if player was absent and is now
     * active again.
     *
     * @param playerName the player to restore
     * @return true if player was restored from absent
     */
    public boolean restoreAbsentPlayer(String playerName) {
        if (playerName == null) {
            return false;
        }
        boolean wasAbsent = absentPlayers.remove(playerName);
        if (wasAbsent && !playerNames.contains(playerName)) {
            playerNames.add(playerName);
            return true;
        }
        return false;
    }

    /**
     * Get the set of absent (gone but not removed) players in this lobby.
     *
     * @return copy of absent players set
     */
    public Set<String> getAbsentPlayers() {
        return Set.copyOf(absentPlayers);
    }

    /**
     * Check if this lobby has any players (active or absent).
     *
     * @return true if playerNames or absentPlayers is non-empty
     */
    public boolean hasAnyPlayers() {
        return !playerNames.isEmpty() || !absentPlayers.isEmpty();
    }

    /**
     * Atomically add a player and, if the lobby reached {@code autoStartPlayers} and no game exists
     * yet, create and start a game. The operation is synchronized on the internal player list to
     * avoid races when multiple joins happen concurrently.
     *
     * @return {@link AddResult} indicating whether the player was added and if a game was started
     */
    public AddResult addPlayerAndMaybeStart(
            String playerName, int maxPlayers, int autoStartPlayers, int defaultStartChips) {
        if (playerName == null) {
            return AddResult.NOT_ADDED;
        }
        synchronized (playerNames) {
            if (playerNames.contains(playerName)) {
                return AddResult.NOT_ADDED;
            }
            if (playerNames.size() >= maxPlayers) {
                return AddResult.NOT_ADDED;
            }
            playerNames.add(playerName);

            // If threshold reached and no game running, create and start game here
            if (gameController == null && playerNames.size() == autoStartPlayers) {
                try {
                    GameState state = new GameState();
                    GameEngine engine =
                            new GameEngine(
                                    state,
                                    new RuleEngine(new ArrayList<>()),
                                    new RoundManager(),
                                    new TurnManager());
                    GameController game = new GameController(engine);

                    for (String p : playerNames) {
                        game.addPlayer(PlayerId.of(p), defaultStartChips);
                    }

                    // Set the callback before starting the game
                    if (onGameEndedCallback != null) {
                        game.setOnGameEndedCallback(onGameEndedCallback);
                    }

                    game.startGame();
                    this.gameController = game;
                    LOGGER.info(() -> "Auto-started game in lobby " + id.value());
                    return AddResult.ADDED_AND_STARTED;
                } catch (RuntimeException e) {
                    LOGGER.log(Level.WARNING, "Auto-start failed for lobby " + id.value(), e);
                    return AddResult.ADDED;
                }
            }

            return AddResult.ADDED;
        }
    }
}
