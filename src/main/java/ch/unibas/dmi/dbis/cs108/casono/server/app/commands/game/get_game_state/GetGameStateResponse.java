package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.GameController;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Rank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Suit;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;
import java.util.List;
import java.util.Objects;

/**
 * Response carrying a snapshot of the current game state using the project's wire format.
 *
 * Wire format (relevant parts):
 *
 * +OK
 *   PHASE=...
 *   POT=...
 *   CURRENT_BET=...
 *   DEALER=...
 *   ACTIVE_PLAYER=...
 *   CARD
 *     VALUE=...
 *     SUIT=...
 *   END
 *   PLAYER
 *     NAME=...
 *     CHIPS=...
 *     BET=...
 *     STATE=...
 *     CARD           (only for requesting user)
 *       VALUE=...
 *       SUIT=...
 *     END
 *   END
 * END
 *
 * Notes:
 * - Community cards are always emitted at top-level (0..5). If none exist (e.g. PREFLOP), none are emitted.
 * - Hole cards are only emitted for the requesting player (privacy).
 */
public class GetGameStateResponse extends SuccessResponse {

    public GetGameStateResponse(RequestContext context, GameController game, String requestingUsername) {
        super(context, buildBody(Objects.requireNonNull(game, "game must not be null").getState(), requestingUsername));
    }

    private static ResponseBody buildBody(GameState state, String requestingUsername) {
        Objects.requireNonNull(state, "state must not be null");

        ResponseBodyBuilder builder = ResponseBody.builder();

        builder.param("PHASE", state.getPhase() == null ? "UNKNOWN" : state.getPhase().name());
        builder.param("POT", state.getPot() == null ? 0 : state.getPot().getAmount());
        builder.param("CURRENT_BET", computeGlobalCurrentBet(state));
        builder.param("DEALER", state.getDealerIndex());
        builder.param("ACTIVE_PLAYER", state.getCurrentPlayerIndex());

        appendCommunityCards(builder, state);

        appendPlayers(builder, state, requestingUsername);

        return builder.build();
    }

    private static int computeGlobalCurrentBet(GameState state) {
        int globalCurrentBet = 0;
        for (Player p : state.getPlayers()) {
            if (p == null) continue;
            int b = state.getCurrentBet(p.getId());
            if (b > globalCurrentBet) globalCurrentBet = b;
        }
        return globalCurrentBet;
    }

    private static void appendCommunityCards(ResponseBodyBuilder builder, GameState state) {
        List<Card> community = state.getCommunityCards();

        if (community == null || community.isEmpty()) {
            return;
        }

        for (Card c : community) {
            if (c == null) continue;
            builder.block("CARD", card -> {
                card.param("VALUE", rankToWire(c.getRank()));
                card.param("SUIT", suitToWire(c.getSuit()));
            });
        }
    }

    private static void appendPlayers(ResponseBodyBuilder builder, GameState state, String requestingUsername) {
        String req = (requestingUsername == null) ? null : requestingUsername.trim();

        for (Player p : state.getPlayers()) {
            if (p == null) continue;

            PlayerId pid = p.getId();
            String name = (pid == null || pid.value() == null) ? "" : pid.value().trim();

            builder.block("PLAYER", pblock -> {
                pblock.param("NAME", name);
                pblock.param("CHIPS", p.getChips());
                pblock.param("BET", pid == null ? 0 : state.getCurrentBet(pid));
                pblock.param("STATE", p.isFolded() ? "FOLDED" : "ACTIVE");

                if (req != null && pid != null && req.equals(name)) {
                    List<Card> hole = state.getHoleCards(pid);

                    if (hole != null) {
                        for (Card hc : hole) {
                            if (hc == null) continue;
                            pblock.block("CARD", cblock -> {
                                cblock.param("VALUE", rankToWire(hc.getRank()));
                                cblock.param("SUIT", suitToWire(hc.getSuit()));
                            });
                        }
                    }
                }
            });
        }
    }

    private static String rankToWire(Rank r) {
        if (r == null) return "";
        return switch (r) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }

    private static String suitToWire(Suit s) {
        if (s == null) return "";
        return switch (s) {
            case HEARTS -> "H";
            case DIAMONDS -> "D";
            case CLUBS -> "C";
            case SPADES -> "S";
        };
    }
}
