package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;

/**
 * The GameClient class is responsible for communicating with the server to retrieve the current
 * game state. It sends a command to the server and parses the response into a structured GameState
 * object.
 */
public class GameClient {

    private final ClientService client;
    private final int gameId;

    /**
     * Constructs a GameClient with the given ClientService for communication.
     *
     * @param client The ClientService instance used to send commands and receive responses from the
     *     server.
     */
    public GameClient(ClientService client, int gameId) {
        this.client = client;
        this.gameId = gameId;
    }

    /**
     * Fetch the current game state from the server by sending a "GET_GAME_STATE"
     *
     * @return A GameState object representing the current state of the game, as parsed
     */
    public GameState fetchGameState() {

        String response = client.processCommand("GET_GAME_STATE\nGAME_ID=" + gameId);

        if (response == null || response.isBlank()) {
            throw new RuntimeException("Empty server response");
        }

        return parse(response);
    }

    /** Send a CALL command to the server to indicate that the player wants to call */
    public void sendCall() {
        client.processCommand("CALL\nGAME_ID=" + gameId);
    }

    /** Send a FOLD command to the server to indicate that the player wants to fold */
    public void sendFold() {
        client.processCommand("FOLD\nGAME_ID=" + gameId);
    }

    /** Send a BET command to the server to indicate that the player wants to bet */
    public void sendBet(int amount) {
        client.processCommand("BET\nGAME_ID=" + gameId + "\nAMOUNT=" + amount);
    }

    /**
     * Send a RAISE command to the server to indicate that the player wants to raise
     *
     * @param amount The amount to raise to
     */
    public void sendRaise(int amount) {
        client.processCommand("RAISE\nGAME_ID=" + gameId + "\nAMOUNT=" + amount);
    }

    /**
     * Parse the raw string response from the server into a structured GameState object.
     *
     * @param input The raw string response from the server representing the game state.
     * @return A GameState object representing the parsed game state.
     */
    private GameState parse(String input) {

        GameState state = new GameState();

        Player currentPlayer = null;
        Card currentCard = null;

        boolean inPlayers = false;
        boolean inPlayerCards = false;
        boolean inCommunityCards = false;

        for (String raw : input.split("\n")) {

            String line = raw.trim();

            if (line.isEmpty() || line.startsWith("+OK")) {
                continue;
            }

            if (line.startsWith("PHASE=")) {
                state.phase = value(line);
            } else if (line.startsWith("POT=")) {
                state.pot = intVal(line);
            } else if (line.startsWith("CURRENT_BET=")) {
                state.currentBet = intVal(line);
            } else if (line.startsWith("DEALER=")) {
                state.dealer = intVal(line);
            } else if (line.startsWith("ACTIVE_PLAYER=")) {
                state.activePlayer = intVal(line);
            } else if (line.startsWith("WINNER=")) {
                state.winnerIndex = intVal(line);
            } else if (line.equals("END")) {
                inPlayers = false;
                inPlayerCards = false;
                inCommunityCards = false;
                currentPlayer = null;
                currentCard = null;
                continue;
            } else if (line.equals("PLAYERS")) {
                inPlayers = true;
                inPlayerCards = false;
                inCommunityCards = false;
                continue;
            } else if (line.equals("PLAYER")) {
                currentPlayer = new Player();
                state.players.add(currentPlayer);
                continue;
            } else if (line.startsWith("NAME=") && currentPlayer != null) {
                currentPlayer.setId(PlayerId.of(value(line)));
            } else if (line.startsWith("CHIPS=") && currentPlayer != null) {
                currentPlayer.setChips(intVal(line));
            } else if (line.startsWith("BET=") && currentPlayer != null) {
                currentPlayer.setBet(intVal(line));
            } else if (line.startsWith("STATE=") && currentPlayer != null) {
                currentPlayer.setState(parseState(value(line)));
            } else if (line.equals("CARDS")) {

                if (inPlayers && currentPlayer != null) {
                    inPlayerCards = true;
                    inCommunityCards = false;
                } else {
                    inCommunityCards = true;
                    inPlayerCards = false;
                }

                continue;
            } else if (line.startsWith("CARD")) {
                currentCard = new Card("", "");

                if (inPlayerCards && currentPlayer != null) {
                    currentPlayer.addCard(currentCard);
                } else if (inCommunityCards) {
                    state.communityCards.add(currentCard);
                }
            } else if (line.startsWith("VALUE=") && currentCard != null) {
                currentCard.setValue(value(line));
            } else if (line.startsWith("SUIT=") && currentCard != null) {
                currentCard.setSuit(value(line));
            }
        }

        return state;
    }

    /**
     * Helper method to extract the value from a line in the format KEY=VALUE.
     *
     * @param line The input line from which to extract the value, expected to be in the format
     *     KEY=VALUE.
     * @return The extracted value part of the input line, which is the substring after the first
     *     '=' character.
     */
    private String value(String line) {
        return line.split("=", 2)[1];
    }

    /**
     * Helper method to extract an integer value from a line in the format KEY=VALUE.
     *
     * @param line The input line from which to extract the integer value, expected to be in the
     *     format KEY=VALUE where VALUE is an integer.
     * @return The extracted integer value from the input line, parsed from the substring after the
     *     first '=' character.
     */
    private int intVal(String line) {
        return Integer.parseInt(value(line));
    }

    /**
     * Helper method to parse a string representation of a player's state into the corresponding
     * PlayerState enum value.
     *
     * @param s The input string representing the player's state, expected to be one of ACTIVE,
     *     FOLDED, or DEALER (case-insensitive).
     * @return The corresponding PlayerState enum value based on the input string. If the input does
     *     not match any known state, it defaults to PlayerState.ACTIVE.
     */
    private PlayerState parseState(String s) {
        return switch (s.toUpperCase()) {
            case "ACTIVE" -> PlayerState.ACTIVE;
            case "FOLDED" -> PlayerState.FOLDED;
            case "DEALER" -> PlayerState.DEALER;
            default -> PlayerState.ACTIVE;
        };
    }
}
