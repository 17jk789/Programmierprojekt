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
 * Protocol notes (based on your current server implementation):
 * - Success replies contain: PHASE, POT, CURRENT_BET, DEALER, ACTIVE_PLAYER, then blocks:
 *   - CARD ... END (community cards on root level)
 *   - PLAYER ... (NAME/CHIPS/BET/STATE + optional CARD blocks for requesting player) ... END
 * - Error replies contain: -ERR then CODE=..., MSG=..., END
 *
 * Important:
 * - The server does NOT wrap cards in a "CARDS" container.
 * - CARD blocks appear either:
 *   - at root level  -> community cards
 *   - inside PLAYER  -> hole cards for that player (usually only for the requesting user)
 */
public class GameClient {

    private static final Logger LOG = Logger.getLogger(GameClient.class.getName());

    private final ClientService client;
    private final int gameId;

    public GameClient(ClientService client, int gameId) {
        if (client == null) throw new IllegalArgumentException("ClientService must not be null");
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
            LOG.info(() -> "Parsed state: phase=" + state.phase
                    + " pot=" + state.pot
                    + " players=" + (state.players != null ? state.players.size() : 0)
                    + " community=" + (state.communityCards != null ? state.communityCards.size() : 0));
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

        if (s.players == null) s.players = new ArrayList<>();
        if (s.communityCards == null) s.communityCards = new ArrayList<>();

        Player currentPlayer = null;
        Card currentCard = null;
        boolean insidePlayer = false;

        for (String raw : input.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("+OK")) continue;

            // Global fields
            if (line.startsWith("PHASE=")) {
                s.phase = value(line);
                continue;
            }
            if (line.startsWith("POT=")) {
                s.pot = intVal(line);
                continue;
            }
            if (line.startsWith("CURRENT_BET=")) {
                s.currentBet = intVal(line);
                continue;
            }
            if (line.startsWith("DEALER=")) {
                s.dealer = intVal(line);
                continue;
            }
            if (line.startsWith("ACTIVE_PLAYER=")) {
                s.activePlayer = intVal(line);
                continue;
            }
            if (line.startsWith("WINNER=")) {
                s.winnerIndex = intVal(line);
                continue;
            }

            if (line.equals("PLAYER")) {
                currentPlayer = new Player();
                s.players.add(currentPlayer);
                currentCard = null;
                insidePlayer = true;
                continue;
            }

            if (line.equals("CARDS")) {
                continue;
            }

            if (line.equals("CARD")) {
                currentCard = new Card("", "");

                if (insidePlayer && currentPlayer != null) {
                    currentPlayer.addCard(currentCard);   // hole card
                } else {
                    s.communityCards.add(currentCard);    // community card
                }
                continue;
            }

            if (line.startsWith("VALUE=") && currentCard != null) {
                currentCard.setValue(value(line));
                continue;
            }
            if (line.startsWith("SUIT=") && currentCard != null) {
                currentCard.setSuit(value(line));
                continue;
            }

            if (line.equals("END")) {
                if (currentCard != null) {        // schließt CARD
                    currentCard = null;
                    continue;
                }
                if (insidePlayer) {               // schließt PLAYER
                    insidePlayer = false;
                    currentPlayer = null;
                    continue;
                }
                continue;                         // root END
            }

            if (currentPlayer != null) {
                if (line.startsWith("USERNAME=") || line.startsWith("NAME=")) {
                    currentPlayer.setId(PlayerId.of(value(line)));
                    continue;
                }
                if (line.startsWith("CHIPS=")) {
                    currentPlayer.setChips(intVal(line));
                    continue;
                }
                if (line.startsWith("BET=")) {
                    currentPlayer.setBet(intVal(line));
                    continue;
                }
                if (line.startsWith("STATE=")) {
                    currentPlayer.setState(parseState(value(line)));
                    continue;
                }
            }

            LOG.fine(() -> "Ignored line: '" + line + "'");
        }

        return s;
    }

    private static String value(String line) {
        return line.split("=", 2)[1];
    }

    private static int intVal(String line) {
        return Integer.parseInt(value(line));
    }

    private static PlayerState parseState(String s) {
        if (s == null) return PlayerState.ACTIVE;
        return switch (s.trim().toUpperCase()) {
            case "ACTIVE" -> PlayerState.ACTIVE;
            case "FOLDED" -> PlayerState.FOLDED;
            case "DEALER" -> PlayerState.DEALER;
            default -> PlayerState.ACTIVE;
        };
    }

    private static String extractValue(String joined, String key) {
        if (joined == null) return null;
        for (String raw : joined.split("\n")) {
            String line = raw.trim();
            if (line.startsWith(key + "=")) {
                return line.substring((key + "=").length()).replace("'", "");
            }
        }
        return null;
    }
}
