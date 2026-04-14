package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerStatus;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 * RoundManager manages hand lifecycle and phase progression (PREFLOP -> FLOP -> TURN -> RIVER ->
 * SHOWDOWN).
 *
 * <p>This implementation:
 *
 * <ul>
 *   <li>starts a new hand (reset + hole cards + blinds)
 *   <li>progresses phases once betting round is finished
 *   <li>deals community cards (3/1/1)
 * </ul>
 */
public class RoundManager {

    public static final int SMALL_BLIND = 100;
    public static final int BIG_BLIND = 200;

    public void startNewHand(GameState state) {
        // Use GameState's canonical reset
        state.startNewHand();

        // Ensure deck exists
        ensureDeck(state);

        // Deal hole cards
        dealHoleCards(state);

        // Post blinds
        postBlinds(state);

        // Preflop always starts left of BB (or dealer in heads-up).
        state.setCurrentPlayerToPreflopFirstToAct();
    }

    public void progressIfNeeded(GameState state) {
        if (state.getPhase() == null) {
            state.setPhase(GamePhase.PREFLOP);
        }

        if (state.countNonFoldedPlayers() == 1) {
            state.setPhase(GamePhase.FINISHED);
            return;
        }

        if (isBettingRoundFinished(state)) {
            advancePhase(state);
        }
    }

    private boolean isBettingRoundFinished(GameState state) {
        int target = state.getTableState().getCurrentBet();

        for (Player p : state.getPlayers()) {
            if (p == null) {
                continue;
            }

            // be tolerant if codebase mixes status + boolean flags
            if (p.getStatus() == PlayerStatus.FOLDED || p.isFolded()) {
                continue;
            }

            int bet = state.getCurrentBet(p.getId());
            if (bet < target && !p.isAllIn()) {
                return false;
            }
        }
        return true;
    }

    private void advancePhase(GameState state) {
        switch (state.getPhase()) {
            case PREFLOP -> dealFlop(state);
            case FLOP -> dealTurn(state);
            case TURN -> dealRiver(state);
            case RIVER -> showdown(state);
            case SHOWDOWN -> state.setPhase(GamePhase.FINISHED);
            case FINISHED -> {
                /* game is over */
            }
        }
    }

    private void ensureDeck(GameState state) {
        Deck deck = state.getDeck();
        if (deck == null) {
            deck = new Deck();
            deck.shuffle();
            state.setDeck(deck);
        }
    }

    private void dealHoleCards(GameState state) {
        Deck deck = state.getDeck();

        for (Player p : state.getPlayers()) {
            if (p == null) {
                continue;
            }

            PlayerId pid = p.getId();
            Card c1 = deck.draw();
            Card c2 = deck.draw();

            state.giveHoleCards(pid, c1, c2);
        }
    }

    /**
     * NOTE: Blind assignment here is intentionally minimal because GameState does not expose
     * seating order. If you want correct dealer-relative blinds, add helpers in GameState (e.g.
     * getPlayerIdAt(int)).
     */
    private void postBlinds(GameState state) {
        List<Player> playerList = new ArrayList<>(state.getPlayers());
        if (playerList.size() < 2) {
            return;
        }

        // pick first two non-folded players as SB/BB
        Player sb = null;
        Player bb = null;

        for (Player p : playerList) {
            if (p == null) {
                continue;
            }

            if (p.isFolded()) {
                continue;
            }

            if (sb == null) {
                sb = p;
            } else {
                bb = p;
                break;
            }
        }

        if (sb == null || bb == null) {
            return;
        }

        sb.removeChips(SMALL_BLIND);
        bb.removeChips(BIG_BLIND);

        state.addToPot(SMALL_BLIND + BIG_BLIND);

        state.setCurrentBet(sb.getId(), SMALL_BLIND);
        state.setCurrentBet(bb.getId(), BIG_BLIND);

        state.getTableState().setCurrentBet(BIG_BLIND);
    }

    private void dealFlop(GameState state) {
        ensureDeck(state);
        Deck deck = state.getDeck();

        state.addCommunityCard(deck.draw());
        state.addCommunityCard(deck.draw());
        state.addCommunityCard(deck.draw());

        state.setPhase(GamePhase.FLOP);

        // new betting round
        state.resetBets();
        state.setCurrentPlayerToPostflopFirstToAct();
    }

    private void dealTurn(GameState state) {
        ensureDeck(state);
        Deck deck = state.getDeck();

        state.addCommunityCard(deck.draw());

        state.setPhase(GamePhase.TURN);

        // new betting round
        state.resetBets();
        state.setCurrentPlayerToPostflopFirstToAct();
    }

    private void dealRiver(GameState state) {
        ensureDeck(state);
        Deck deck = state.getDeck();

        state.addCommunityCard(deck.draw());

        state.setPhase(GamePhase.RIVER);

        // new betting round
        state.resetBets();
        state.setCurrentPlayerToPostflopFirstToAct();
    }

    private void showdown(GameState state) {
        state.setPhase(GamePhase.SHOWDOWN);

        // winner evaluation is elsewhere (rules/controller)
        // do NOT reset cards here; clients still need board visible during showdown
    }
}
