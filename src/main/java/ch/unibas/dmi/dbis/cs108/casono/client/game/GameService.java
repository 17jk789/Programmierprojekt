package ch.unibas.dmi.dbis.cs108.casono.client.game;

import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameService {

    private static final Logger LOG = Logger.getLogger(GameService.class.getName());

    private final GameClient client;
    private GameState state;

    public GameService(GameClient client) {
        if (client == null) throw new IllegalArgumentException("GameClient must not be null");
        this.client = client;
        LOG.info("GameService created");
    }

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

        LOG.info(() -> "State updated: phase=" + state.phase
                + " pot=" + state.pot
                + " players=" + (state.players != null ? state.players.size() : 0)
                + " community=" + (state.communityCards != null ? state.communityCards.size() : 0));

        return state;
    }

    public int getPot() {
        ensureState();
        return state.pot;
    }

    public List<Card> getCommunityCards() {
        ensureState();
        return state.communityCards != null ? state.communityCards : List.of();
    }

    public List<Player> getPlayers() {
        ensureState();
        return state.players != null ? state.players : List.of();
    }

    public Player getWinner() {
        ensureState();
        if (state.winnerIndex < 0 || state.players == null || state.winnerIndex >= state.players.size()) {
            return null;
        }
        return state.players.get(state.winnerIndex);
    }

    public void call() { client.sendCall(); }
    public void fold() { client.sendFold(); }
    public void bet(int amount) { client.sendBet(amount); }
    public void raise(int amount) { client.sendRaise(amount); }

    private void ensureState() {
        if (state == null) {
            throw new IllegalStateException("GameService used before any successful refresh()");
        }
    }

    public GameState peekStateOrNull() {
        return state;
    }
}
