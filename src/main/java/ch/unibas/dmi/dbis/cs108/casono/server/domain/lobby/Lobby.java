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
    private volatile GameController gameController;

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
    }

    public GameController getGameController() {
        return gameController;
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
