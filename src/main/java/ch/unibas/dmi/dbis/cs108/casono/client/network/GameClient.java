package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import java.util.List;

/**
 * The GameClient class is responsible for communicating with the server to retrieve the current
 * game state. It sends a command to the server and parses the response into a structured GameState
 * object.
 */
public class GameClient {

    private final ClientService client;

    /**
     * Constructs a GameClient with the given ClientService for communication.
     *
     * @param client The ClientService instance used to send commands and receive responses from the
     *     server.
     */
    public GameClient(ClientService client) {
        this.client = client;
    }

    /**
     * Retrieves the current game state from the server by sending a command and parsing the
     * response.
     *
     * @return A GameState object representing the current state of the game.
     */
    public GameState getGameState() {
        List<String> response = client.processCommand("GET_GAME_STATE");
        return parseGameState(response);
    }

    /**
     * Parses the raw response from the server into a structured GameState object.
     *
     * @param input The raw response string from the server.
     * @return A GameState object representing the current state of the game.
     */
    private GameState parseGameState(List<String> input) {

        GameState state = new GameState();

        // String[] lines = input.split("\n");

        Player currentPlayer = null;
        Card currentCard = null;

        for (String rawLine : input) {

            String line = rawLine.trim();

            if (line.startsWith("+OK") || line.equals("END")) {
                continue;
            }

            if (line.startsWith("PHASE=")) {
                state.phase = line.split("=")[1];
            } else if (line.startsWith("POT=")) {
                state.pot = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("CURRENT_BET=")) {
                state.currentBet = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("DEALER=")) {
                state.dealer = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("ACTIVE_PLAYER=")) {
                state.activePlayer = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("PLAYER")) {
                currentPlayer = new Player();
                state.players.add(currentPlayer);
            } else if (line.startsWith("NAME=") && currentPlayer != null) {
                currentPlayer.name = line.split("=")[1];
            } else if (line.startsWith("CHIPS=") && currentPlayer != null) {
                currentPlayer.chips = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("BET=") && currentPlayer != null) {
                currentPlayer.bet = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("STATE=") && currentPlayer != null) {
                currentPlayer.state = line.split("=")[1];
            } else if (line.startsWith("CARD")) {
                currentCard = new Card();

                if (currentPlayer != null) {
                    currentPlayer.cards.add(currentCard);
                } else {
                    state.communityCards.add(currentCard);
                }
            } else if (line.startsWith("VALUE=") && currentCard != null) {
                currentCard.value = line.split("=")[1];
            } else if (line.startsWith("SUIT=") && currentCard != null) {
                currentCard.suit = line.split("=")[1];
            }
        }

        return state;
    }
}
