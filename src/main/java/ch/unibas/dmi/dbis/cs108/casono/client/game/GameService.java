package ch.unibas.dmi.dbis.cs108.casono.client.game;

import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
import java.util.List;

/**
 * Service class responsible for managing the game state and providing methods to interact with the
 * game.
 */
public class GameService {

    private final GameClient client;
    private GameState state;

    /**
     * Constructs a GameService with the given GameClient for communication with the server.
     *
     * @param client The GameClient instance used to send commands and receive responses from the
     *     server.
     */
    public GameService(GameClient client) {
        this.client = client;
    }

    /**
     * Refresh the game state by fetching the latest state from the server using the GameClient.
     *
     * @return The updated GameState object representing the current state of the game after
     *     refreshing.
     */
    public GameState refresh() {
        state = client.fetchGameState();
        return state;
    }

    /**
     * Get the current game state.
     *
     * @return The current GameState object representing the state of the game.
     */
    public int getPot() {
        return state.pot;
    }

    /**
     * Get the current game state.
     *
     * @return The current GameState object representing the state of the game.
     */
    public List<Card> getCommunityCards() {
        return state.communityCards;
    }

    /**
     * Get the list of players in the current game state.
     *
     * @return A list of Player objects representing the players in the current game state.
     */
    public List<Player> getPlayers() {
        return state.players;
    }

    /**
     * Send a CALL command to the server to indicate that the player wants to call.
     */
    public void call() {
        client.sendCall();
    }

    /**
     * Send a FOLD command to the server to indicate that the player wants to fold.
     */
    public void fold() {
        client.sendFold();
    }

    /**
     * Send a BET command to the server to indicate that the player wants to bet with the specified
     * amount.
     *
     * @param amount The amount the player wants to bet.
     */
    public void bet(int amount) {
        client.sendBet(amount);
    }

    /**
     * Send a RAISE command to the server to indicate that the player wants to raise to the
     * specified amount.
     *
     * @param amount The amount the player wants to raise to.
     */
    public void raise(int amount) {
        client.sendRaise(amount);
    }

    /**
     * Get the winner of the game if there is one. If the game is still ongoing or if there is no
     * winner, this method returns null.
     *
     * @return The Player object representing the winner of the game, or null if there is no winner
     *     yet.
     */
    public Player getWinner() {
        if (state.winnerIndex < 0) {
            return null;
        }

        return state.players.get(state.winnerIndex);
    }
}
