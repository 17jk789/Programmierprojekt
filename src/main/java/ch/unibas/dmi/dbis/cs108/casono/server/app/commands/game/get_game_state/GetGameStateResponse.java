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

/** Response carrying a snapshot of the current game state using the project's wire format. */
public class GetGameStateResponse extends SuccessResponse {
    public GetGameStateResponse(
            RequestContext context, GameController game, String requestingUsername) {
        super(context, buildBody(game.getState(), requestingUsername));
    }

    private static ResponseBody buildBody(GameState state, String requestingUsername) {
        int globalCurrentBet = computeGlobalCurrentBet(state);

        ResponseBodyBuilder builder = ResponseBody.builder();
        builder.param("PHASE", state.getPhase() == null ? "UNKNOWN" : state.getPhase().name());
        builder.param("POT", state.getPot().getAmount());
        builder.param("CURRENT_BET", globalCurrentBet);
        builder.param("DEALER", state.getDealerIndex());
        builder.param("ACTIVE_PLAYER", state.getCurrentPlayerIndex());

        appendCommunityCards(builder, state);
        appendPlayers(builder, state, requestingUsername);

        return builder.build();
    }

    private static int computeGlobalCurrentBet(GameState state) {
        int globalCurrentBet = 0;
        for (Player p : state.getPlayers()) {
            int b = state.getCurrentBet(p.getId());
            if (b > globalCurrentBet) {
                globalCurrentBet = b;
            }
        }
        return globalCurrentBet;
    }

    private static void appendCommunityCards(ResponseBodyBuilder builder, GameState state) {
        for (Card c : state.getCommunityCards()) {
            builder.block(
                    "CARD",
                    card -> {
                        card.param("VALUE", rankToWire(c.getRank()));
                        card.param("SUIT", suitToWire(c.getSuit()));
                    });
        }
    }

    private static void appendPlayers(
            ResponseBodyBuilder builder, GameState state, String requestingUsername) {
        for (Player p : state.getPlayers()) {
            PlayerId pid = p.getId();
            builder.block(
                    "PLAYER",
                    pblock -> {
                        pblock.param("NAME", pid.value());
                        pblock.param("CHIPS", p.getChips());
                        pblock.param("BET", state.getCurrentBet(pid));
                        pblock.param("STATE", p.isFolded() ? "FOLDED" : "ACTIVE");

                        if (requestingUsername != null && requestingUsername.equals(pid.value())) {
                            List<Card> hole = state.getHoleCards(pid);
                            for (Card hc : hole) {
                                pblock.block(
                                        "CARD",
                                        cblock -> {
                                            cblock.param("VALUE", rankToWire(hc.getRank()));
                                            cblock.param("SUIT", suitToWire(hc.getSuit()));
                                        });
                            }
                        }
                    });
        }
    }

    private static String rankToWire(Rank r) {
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
        return switch (s) {
            case HEARTS -> "H";
            case DIAMONDS -> "D";
            case CLUBS -> "C";
            case SPADES -> "S";
        };
    }
}
