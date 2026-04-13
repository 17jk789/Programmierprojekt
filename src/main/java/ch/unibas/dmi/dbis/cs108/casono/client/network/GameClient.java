package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches and parses game state from server.
 *
 * <p>Protocol notes (based on your current server implementation): - Success replies contain:
 * PHASE, POT, CURRENT_BET, DEALER, ACTIVE_PLAYER, then blocks: - CARD ... END (community cards on
 * root level) - PLAYER ... (NAME/CHIPS/BET/STATE + optional CARD blocks for requesting player) ...
 * END - Error replies contain: -ERR then CODE=..., MSG=..., END
 *
 * <p>Important: - The server does NOT wrap cards in a "CARDS" container. - CARD blocks appear
 * either: - at root level -> community cards - inside PLAYER -> hole cards for that player (usually
 * only for the requesting user)
 */
public class GameClient {

    private static final Logger LOG = Logger.getLogger(GameClient.class.getName());

    private final ClientService client;
    private final int gameId;

    public GameClient(ClientService client, int gameId) {
        if (client == null) {
            throw new IllegalArgumentException("ClientService must not be null");
        }

        this.client = client;
        this.gameId = gameId;
        LOG.info(() -> "GameClient initialized for gameId=" + gameId);
    }

    public GameState fetchGameState() {
        final String cmd = "GET_GAME_STATE GAME_ID=" + gameId;

        List<String> lines;
        try {
            lines = client.processCommand(cmd);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "processCommand failed for " + cmd + ": " + e.getMessage(), e);
            return null;
        }

        if (lines == null || lines.isEmpty()) {
            LOG.warning("Empty server response for " + cmd);
            return null;
        }

        LOG.info(() -> "GET_GAME_STATE raw lines count=" + lines.size() + " lines=" + lines);

        String joined = String.join("\n", lines);

        if (joined.contains("-ERR") || joined.contains("-ERROR")) {
            String code = extractValue(joined, "CODE");
            String msg = extractValue(joined, "MSG");
            LOG.info(() -> "GET_GAME_STATE returned -ERR code=" + code + " msg=" + msg);

            if ("GAME_NOT_STARTED".equalsIgnoreCase(code)) {
                return null;
            }
            return null;
        }

        try {
            GameState state = parseGameState(joined);
            LOG.info(
                    () ->
                            "Parsed state: phase="
                                    + state.phase
                                    + " pot="
                                    + state.pot
                                    + " players="
                                    + (state.players != null ? state.players.size() : 0)
                                    + " community="
                                    + (state.communityCards != null
                                            ? state.communityCards.size()
                                            : 0));
            return state;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed parsing game state. Payload=\n" + joined, e);
            return null;
        }
    }

    public void sendCall() {
        client.processCommand("CALL GAME_ID=" + gameId);
    }

    public void sendFold() {
        client.processCommand("FOLD GAME_ID=" + gameId);
    }

    public void sendBet(int amount) {
        client.processCommand("BET GAME_ID=" + gameId + " AMOUNT=" + amount);
    }

    public void sendRaise(int amount) {
        client.processCommand("RAISE GAME_ID=" + gameId + " AMOUNT=" + amount);
    }

    private GameState parseGameState(String input) {
        GameState s = new GameState();
        s.players = new ArrayList<>();
        s.communityCards = new ArrayList<>();

        ParseState state = new ParseState();
        for (String raw : input.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("+OK")) {
                continue;
            }

            if (!parseGlobalField(s, line)
                    && !parsePlayerStructure(s, state, line)
                    && !parseCardData(state, line)
                    && !parsePlayerData(state, line)) {
                LOG.fine(() -> "Ignored line: '" + line + "'");
            }
        }
        return s;
    }

    private boolean parseGlobalField(GameState s, String line) {
        return switch (line) {
            case String l when l.startsWith("PHASE=") -> {
                s.phase = value(l);
                yield true;
            }
            case String l when l.startsWith("POT=") -> {
                s.pot = intVal(l);
                yield true;
            }
            case String l when l.startsWith("CURRENT_BET=") -> {
                s.currentBet = intVal(l);
                yield true;
            }
            case String l when l.startsWith("DEALER=") -> {
                s.dealer = intVal(l);
                yield true;
            }
            case String l when l.startsWith("ACTIVE_PLAYER=") -> {
                s.activePlayer = intVal(l);
                yield true;
            }
            case String l when l.startsWith("WINNER=") -> {
                s.winnerIndex = intVal(l);
                yield true;
            }
            default -> false;
        };
    }

    private boolean parsePlayerStructure(GameState s, ParseState state, String line) {
        if (line.equals("PLAYER")) {
            state.currentPlayer = new Player();
            s.players.add(state.currentPlayer);
            state.currentCard = null;
            state.insidePlayer = true;
            return true;
        }

        if (line.equals("CARDS")) {
            return true;
        }

        if (line.equals("CARD")) {
            state.currentCard = new Card("", "");
            if (state.insidePlayer && state.currentPlayer != null) {
                state.currentPlayer.addCard(state.currentCard);
            } else {
                s.communityCards.add(state.currentCard);
            }
            return true;
        }

        if (line.equals("END")) {
            if (state.currentCard != null) {
                state.currentCard = null;
            } else if (state.insidePlayer) {
                state.insidePlayer = false;
                state.currentPlayer = null;
            }
            return true;
        }
        return false;
    }

    private boolean parseCardData(ParseState state, String line) {
        if (state.currentCard == null) {
            return false;
        }

        if (line.startsWith("VALUE=")) {
            state.currentCard.setValue(value(line));
            return true;
        }
        if (line.startsWith("SUIT=")) {
            state.currentCard.setSuit(value(line));
            return true;
        }
        return false;
    }

    private boolean parsePlayerData(ParseState state, String line) {
        if (state.currentPlayer == null) {
            return false;
        }

        if (line.startsWith("USERNAME=") || line.startsWith("NAME=")) {
            state.currentPlayer.setId(PlayerId.of(value(line)));
            return true;
        }

        if (line.startsWith("CHIPS=")) {
            state.currentPlayer.setChips(intVal(line));
            return true;
        }

        if (line.startsWith("BET=")) {
            state.currentPlayer.setBet(intVal(line));
            return true;
        }

        if (line.startsWith("STATE=")) {
            state.currentPlayer.setState(parseState(value(line)));
            return true;
        }

        return false;
    }

    private static class ParseState {
        Player currentPlayer;
        Card currentCard;
        boolean insidePlayer;
    }

    private static String value(String line) {
        return line.split("=", 2)[1];
    }

    private static int intVal(String line) {
        return Integer.parseInt(value(line));
    }

    private static PlayerState parseState(String s) {
        if (s == null) {
            return PlayerState.ACTIVE;
        }

        return switch (s.trim().toUpperCase()) {
            case "ACTIVE" -> PlayerState.ACTIVE;
            case "FOLDED" -> PlayerState.FOLDED;
            case "DEALER" -> PlayerState.DEALER;
            default -> PlayerState.ACTIVE;
        };
    }

    private static String extractValue(String joined, String key) {
        if (joined == null) {
            return null;
        }

        for (String raw : joined.split("\n")) {
            String line = raw.trim();
            if (line.startsWith(key + "=")) {
                return line.substring((key + "=").length()).replace("'", "");
            }
        }
        return null;
    }
}
