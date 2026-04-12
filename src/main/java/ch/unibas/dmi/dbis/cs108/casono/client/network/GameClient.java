package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import java.util.List;

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
        List<String> responseLines = client.processCommand("GET_GAME_STATE GAME_ID=" + gameId);

        if (responseLines == null || responseLines.isEmpty()) {
            throw new RuntimeException("Empty server response");
        }

        if (responseLines.get(0).startsWith("-ERR")) {
            throw new RuntimeException("Server Error: " + String.join(" ", responseLines));
        }

        if (!responseLines.get(0).startsWith("+OK")) {
            throw new RuntimeException("Invalid response: missing +OK");
        }

        String fullResponse = String.join("\n", responseLines);
        return parse(fullResponse);
    }

    /** Send a CALL command to the server to indicate that the player wants to call */
    public void sendCall() {
        client.processCommand("CALL GAME_ID=" + gameId);
    }

    /** Send a FOLD command to the server to indicate that the player wants to fold */
    public void sendFold() {
        client.processCommand("FOLD GAME_ID=" + gameId);
    }

    /** Send a BET command to the server to indicate that the player wants to bet */
    public void sendBet(int amount) {
        client.processCommand("BET GAME_ID=" + gameId + " AMOUNT=" + amount);
    }

    /**
     * Send a RAISE command to the server to indicate that the player wants to raise
     *
     * @param amount The amount to raise to
     */
    public void sendRaise(int amount) {
        client.processCommand("RAISE GAME_ID=" + gameId + " AMOUNT=" + amount);
    }

    /**
     * Parses the raw server response string into a structured GameState object.
     *
     * @param input The raw server response string containing the game state information.
     * @return A GameState object representing the current state of the game.
     */
    private GameState parse(String input) {

        if (input.startsWith("-ERR")) {
            throw new RuntimeException("Server returned Error:\n" + input);
        }

        GameState state = new GameState();
        ParserContext ctx = new ParserContext(state);

        for (String raw : input.split("\n")) {
            String line = raw.trim();

            if (shouldSkip(line)) {
                continue;
            }

            if (handleGlobal(line, ctx)) {
                continue;
            }

            if (handleSections(line, ctx)) {
                continue;
            }

            if (handlePlayer(line, ctx)) {
                continue;
            }

            if (handleCards(line, ctx)) {
                continue;
            }
        }

        return state;
    }

    /** Holds the mutable parsing state while processing the server response. */
    private static class ParserContext {
        GameState state;
        Player currentPlayer;
        Card currentCard;

        boolean inPlayers;
        boolean inPlayerCards;
        boolean inCommunityCards;

        ParserContext(GameState state) {
            this.state = state;
        }
    }

    /**
     * Checks whether a line should be ignored.
     *
     * @param line the current input line
     * @return true if the line is empty or a status message, false otherwise
     */
    private boolean shouldSkip(String line) {
        return line.isEmpty() || line.startsWith("+OK");
    }

    /**
     * Parses global game state properties (e.g., phase, pot, current bet).
     *
     * @param line the current input line
     * @param ctx the parser context containing the game state
     * @return true if the line was handled, false otherwise
     */
    private boolean handleGlobal(String line, ParserContext ctx) {
        GameState state = ctx.state;

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
        } else {
            return false;
            // throw new RuntimeException("Unknown global field: " + line);
        }
        return true;
    }

    /**
     * Processes section markers such as PLAYERS, PLAYER, CARDS, and END. Updates the parser context
     * accordingly.
     *
     * @param line the current input line
     * @param ctx the parser context
     * @return true if the line was handled, false otherwise
     */
    private boolean handleSections(String line, ParserContext ctx) {

        if (line.equals("END")) {
            // ctx.inPlayers = false;
            ctx.inPlayerCards = false;
            ctx.inCommunityCards = false;
            ctx.currentPlayer = null;
            ctx.currentCard = null;
            return true;
        }

        if (line.equals("PLAYERS")) {
            ctx.inPlayers = true;
            return true;
        }

        if (line.equals("PLAYER")) {
            ctx.currentPlayer = new Player();
            ctx.state.players.add(ctx.currentPlayer);
            return true;
        }

        if (line.equals("CARDS")) {
            if (ctx.inPlayers && ctx.currentPlayer != null) {
                ctx.inPlayerCards = true;
                ctx.inCommunityCards = false;
            } else {
                ctx.inCommunityCards = true;
                ctx.inPlayerCards = false;
            }

            return true;
        }

        return false;
    }

    /**
     * Parses properties of the current player (e.g., name, chips, bet, state).
     *
     * @param line the current input line
     * @param ctx the parser context
     * @return true if the line was handled, false otherwise
     */
    private boolean handlePlayer(String line, ParserContext ctx) {
        Player p = ctx.currentPlayer;

        if (p == null) {
            return false;
        }

        if (line.startsWith("NAME=")) {
            p.setId(PlayerId.of(value(line)));
        } else if (line.startsWith("CHIPS=")) {
            p.setChips(intVal(line));
        } else if (line.startsWith("BET=")) {
            p.setBet(intVal(line));
        } else if (line.startsWith("STATE=")) {
            p.setState(parseState(value(line)));
        } else {
            return false;
        }

        return true;
    }

    /**
     * Parses card-related lines and assigns cards to the current player or the community cards.
     *
     * @param line the current input line
     * @param ctx the parser context
     * @return true if the line was handled, false otherwise
     */
    private boolean handleCards(String line, ParserContext ctx) {

        if (line.startsWith("CARD")) {
            ctx.currentCard = new Card("", "");

            if (ctx.inPlayerCards && ctx.currentPlayer != null) {
                ctx.currentPlayer.addCard(ctx.currentCard);
            } else if (ctx.inCommunityCards) {
                ctx.state.communityCards.add(ctx.currentCard);
            }

            return true;
        }

        if (line.startsWith("VALUE=") && ctx.currentCard != null) {
            ctx.currentCard.setValue(value(line));
            return true;
        }

        if (line.startsWith("SUIT=") && ctx.currentCard != null) {
            ctx.currentCard.setSuit(value(line));
            return true;
        }

        return false;
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
