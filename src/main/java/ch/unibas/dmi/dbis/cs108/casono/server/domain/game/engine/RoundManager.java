package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
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

    /**
     * Starts a new hand by resetting the game state, ensuring a deck is available, dealing hole
     * cards to players, posting blinds, and setting the first player to act for the preflop phase.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to
     */
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

    /**
     * Checks if the current betting round is finished and advances the game phase if necessary.
     *
     * @param state The GameState object representing the current state of the game, which may be
     *     modified to advance the phase or end the hand.
     */
    public void progressIfNeeded(GameState state) {
        if (state.getPhase() == null) {
            state.setPhase(GamePhase.PREFLOP);
        }

        if (state.countNonFoldedPlayers() == 1) {
            state.setHandActive(false);
            state.setPhase(GamePhase.FINISHED);
            return;
        }

        if (isBettingRoundFinished(state)) {
            advancePhase(state);
        }
    }

    /**
     * Determines if the current betting round is finished by checking if all active players have
     * met the current bet or are all-in and by handling special cases for preflop betting rounds.
     *
     * @param state The GameState object representing the current state of the game, which is used
     *     to evaluate the betting round status.
     * @return true if the betting round is finished and the game can progress to the next phase,
     *     false otherwise.
     */
    private boolean isBettingRoundFinished(GameState state) {
        // A betting round only ends after every active player had at least one chance to act.
        return allActivePlayersActedThisRound(state);
    }

    /**
     * Helper method to check if all active players have acted in the current betting round. This is
     * used to handle the special case of preflop rounds where no bets have been made yet, but
     * players still need to have the opportunity to act.
     *
     * @param state The GameState object representing the current state of the game, which is used
     *     to check if all active players have acted in the current round.
     * @return true if all active players have acted in the current round, false if there are still
     *     active players who have not acted yet.
     */
    private boolean allActivePlayersActedThisRound(GameState state) {
        for (Player p : state.getPlayers()) {
            if (p == null || p.isFolded() || p.isAllIn()) {
                continue;
            }

            if (!state.hasActedThisRound(p.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Advances the game phase to the next stage (FLOP, TURN, RIVER, SHOWDOWN) based on the current
     * phase.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to advance the phase and deal community cards as needed when progressing to the
     *     next stage of the hand.
     */
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

    /**
     * Ensures that a deck of cards is available in the game state.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to include a new shuffled deck if one does not already exist.
     */
    private void ensureDeck(GameState state) {
        Deck deck = state.getDeck();
        if (deck == null) {
            deck = new Deck();
            deck.shuffle();
            state.setDeck(deck);
        }
    }

    /**
     * Deals hole cards to each player in the game state by drawing two cards from the deck for each
     * player and assigning them as their hole cards.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to assign hole cards to each active player.
     */
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

    /**
     * Deals the flop by drawing three community cards from the deck and adding them to the game
     * state, then setting the game phase to FLOP and resetting bets for the new betting round.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to add three community cards for the flop, set the phase to FLOP, and reset bets
     *     for the new betting round.
     */
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

    /**
     * Deals the turn by drawing one community card from the deck and adding it to the game state,
     * then setting the game phase to TURN and resetting bets for the new betting round.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to add one community card for the turn, set the phase to TURN, and reset bets
     *     for the new betting round.
     */
    private void dealTurn(GameState state) {
        ensureDeck(state);
        Deck deck = state.getDeck();

        state.addCommunityCard(deck.draw());

        state.setPhase(GamePhase.TURN);

        // new betting round
        state.resetBets();
        state.setCurrentPlayerToPostflopFirstToAct();
    }

    /**
     * Deals the river by drawing one community card from the deck and adding it to the game state,
     * then setting the game phase to RIVER and resetting bets for the new betting round.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to add one community card for the river, set the phase to RIVER, and reset bets
     *     for the new betting round.
     */
    private void dealRiver(GameState state) {
        ensureDeck(state);
        Deck deck = state.getDeck();

        state.addCommunityCard(deck.draw());

        state.setPhase(GamePhase.RIVER);

        // new betting round
        state.resetBets();
        state.setCurrentPlayerToPostflopFirstToAct();
    }

    /**
     * Handles the showdown phase by setting the game phase to SHOWDOWN.
     *
     * @param state The GameState object representing the current state of the game, which will be
     *     modified to set the phase to SHOWDOWN.
     */
    private void showdown(GameState state) {
        state.setPhase(GamePhase.SHOWDOWN);

        // winner evaluation is elsewhere (rules/controller)
        // do NOT reset cards here; clients still need board visible during showdown
    }
}
