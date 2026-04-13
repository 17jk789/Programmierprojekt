package ch.unibas.dmi.dbis.cs108.casono.client.game;

import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameService {

    private static final Logger LOG = Logger.getLogger(GameService.class.getName());

    private final GameClient client;
    private GameState state;

    /**
     * Constructs a GameService with the specified GameClient.
     *
     * @param client The GameClient used to communicate with the server. Must not be null.
     */
    public GameService(GameClient client) {
        if (client == null) {
            throw new IllegalArgumentException("GameClient must not be null");
        }

        this.client = client;
        LOG.info("GameService created");
    }

    /**
     * Refreshes the game state by fetching the latest state from the server using the GameClient.
     *
     * @return The updated GameState after refreshing.
     */
    public GameState refresh() {
        LOG.fine("Refreshing game state...");

        GameState newState;
        try {
            newState = client.fetchGameState();
        } catch (Exception e) {
            // Shouldn't happen with the new GameClient, but keep service stable.
            LOG.log(Level.WARNING, "refresh() failed: " + e.getMessage(), e);
            return state;
        }

        if (newState == null) {
            LOG.fine("No new state (null) -> keeping previous state");
            return state;
        }

        this.state = newState;

        LOG.info(
                () ->
                        "State updated: phase="
                                + state.phase
                                + " pot="
                                + state.pot
                                + " players="
                                + (state.players != null ? state.players.size() : 0)
                                + " community="
                                + (state.communityCards != null ? state.communityCards.size() : 0));

        return state;
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @return The current game phase as a string (e.g., "Pre-Flop", "Flop", "Turn", "River").
     */
    public int getPot() {
        ensureState();
        return state.pot;
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @return The current game phase as a string (e.g., "Pre-Flop", "Flop", "Turn", "River").
     */
    public List<Card> getCommunityCards() {
        ensureState();
        return state.communityCards != null ? state.communityCards : List.of();
    }

    /**
     * Retrieves the list of players currently in the game.
     *
     * @return A list of Player objects representing the players in the game. A list of Player
     *     objects representing the players in the game.
     */
    public List<Player> getPlayers() {
        ensureState();
        return state.players != null ? state.players : List.of();
    }

    /**
     * Retrieves the index of the current player whose turn it is.
     *
     * @return The index of the current player in the players list, or -1 if not available.
     */
    public Player getWinner() {
        ensureState();
        if (state.winnerIndex < 0
                || state.players == null
                || state.winnerIndex >= state.players.size()) {
            return null;
        }
        return state.players.get(state.winnerIndex);
    }

    /** Retrieves the current phase of the game. */
    public void call() {
        client.sendCall();
    }

    /** Retrieves the current phase of the game. */
    public void fold() {
        client.sendFold();
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @param amount The amount to bet. Must be a positive integer.
     */
    public void bet(int amount) {
        client.sendBet(amount);
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @param amount The amount to raise. Must be a positive integer.
     */
    public void raise(int amount) {
        client.sendRaise(amount);
    }

    /** Ensures that the game state has been initialized before accessing it. */
    private void ensureState() {
        if (state == null) {
            throw new IllegalStateException("GameService used before any successful refresh()");
        }
    }

    /**
     * Returns the current GameState without refreshing from the server.
     *
     * @return The current GameState, or null if it has not been initialized yet.
     */
    public GameState peekStateOrNull() {
        return state;
    }
}
