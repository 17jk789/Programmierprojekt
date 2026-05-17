package ch.unibas.dmi.dbis.cs108.casono.client.game;

import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameService {

    private static final Logger LOG = Logger.getLogger(GameService.class.getName());

    private final GameClient client;
    private GameState state;
    private final Queue<Runnable> actionQueue = new LinkedList<>();
    private volatile boolean actionInProgress = false;
    private static final long ACTION_TIMEOUT_MS = 5000;

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
        queueAction(() -> client.sendCall());
    }

    /** Retrieves the current phase of the game. */
    public void fold() {
        queueAction(() -> client.sendFold());
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @param amount The amount to bet. Must be a positive integer.
     */
    public void bet(int amount) {
        queueAction(() -> client.sendBet(amount));
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @param amount The amount to raise. Must be a positive integer.
     */
    public void raise(int amount) {
        queueAction(() -> client.sendRaise(amount));
    }

    /**
     * Queue an action to be executed sequentially. Only one action is processed at a time to
     * prevent concurrent operations from overwhelming the server or UI.
     *
     * @param action The action to queue and execute.
     */
    private void queueAction(Runnable action) {
        synchronized (actionQueue) {
            actionQueue.offer(action);
            if (!actionInProgress) {
                processNextAction();
            }
        }
    }

    /**
     * Process the next queued action if one is available. This method ensures that only one action
     * is in progress at any given time.
     */
    private void processNextAction() {
        synchronized (actionQueue) {
            if (actionQueue.isEmpty() || actionInProgress) {
                return;
            }

            actionInProgress = true;
            Runnable action = actionQueue.poll();

            if (action != null) {
                try {
                    LOG.fine("Processing queued action");
                    action.run();
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Error executing queued action: " + e.getMessage(), e);
                } finally {
                    actionInProgress = false;
                    if (!actionQueue.isEmpty()) {
                        processNextAction();
                    }
                }
            } else {
                actionInProgress = false;
            }
        }
    }

    /**
     * Check if an action is currently in progress, preventing the UI from accepting new actions
     * while one is being processed.
     *
     * @return true if an action is in progress, false otherwise.
     */
    public boolean isActionInProgress() {
        return actionInProgress;
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
