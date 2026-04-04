package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerStatus;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 * RoundManager is responsible for managing the flow of a poker game round. It
 * handles the progression of the game through its various phases (pre-flop,
 * flop, turn, river) and manages player actions such as posting blinds and
 * dealing cards. The RoundManager ensures that the game state is updated
 * correctly based on player actions and the current phase of the game.
 */
public class RoundManager {

    public static final int SMALL_BLIND = 100;
    public static final int BIG_BLIND = 200;

    /**
     * Starts a new hand by initializing the game state, dealing cards to players,
     * and posting blinds. This method sets the hand as active and resets any
     * necessary state variables to prepare for a new round of play.
     *
     * @param state The game state to be used for starting the new hand.
     */
    public void startNewHand(GameState state) {
        state.setHandActive(true);
        state.resetBets();

        dealCards(state);

        postBlinds(state);
    }

    /**
     * Checks if the betting round is finished and advances the game phase if
     * necessary. This method evaluates the current bets of all active players and
     * determines if the betting round can be concluded. If all players have met
     * the current bet or are all-in, the game phase is advanced to the next stage.
     *
     * @param state The current game state to be evaluated for betting round
     *              progression.
     */
    public void progressIfNeeded(GameState state) {

        if (isBettingRoundFinished(state)) {
            advancePhase(state);
        }
    }

    /**
     * Determines if the betting round is finished by checking if all active players
     * have met the current bet or are all-in. This method iterates through all
     * players in the game state and evaluates their bets against the current bet on
     * the table.
     *
     * @param state The current game state to be evaluated for betting round
     *              completion.
     * @return true if the betting round is finished, false otherwise.
     */
    private boolean isBettingRoundFinished(GameState state) {

        int target = state.getTableState().getCurrentBet();

        for (Player p : state.getPlayers()) {

            if (p.getStatus() == PlayerStatus.FOLDED) {
                continue;
            }

            int bet = state.getCurrentBet(p.getId());

            // The player must have placed at least one bet
            if (bet < target && !p.isAllIn()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Advances the game phase to the next stage (flop, turn, river, or showdown)
     * based on the current phase of the game. This method is called when the
     * betting round is finished and updates the game state accordingly to reflect
     * the new phase of play.
     *
     * @param state The current game state to be updated with the new phase.
     */
    private void advancePhase(GameState state) {

        switch (state.getPhase()) {
            case PREFLOP -> dealFlop(state);
            case FLOP -> dealTurn(state);
            case TURN -> dealRiver(state);
            case RIVER -> showdown(state);
        }
    }

    /**
     * Handles the posting of blinds at the start of a new hand. This method
     * identifies the players responsible for posting the small and big blinds,
     * updates their chip counts, adds the blind amounts to the pot, and updates
     * the current bets for those players in the game state.
     *
     * @param state The current game state to be updated with the posted blinds.
     */
    private void postBlinds(GameState state) {
        List<Player> playerList = new ArrayList<>(state.getPlayers());

        Player sb = playerList.get(0);
        Player bb = playerList.get(1);

        int smallBlind = SMALL_BLIND;
        int bigBlind = BIG_BLIND;

        sb.removeChips(smallBlind);
        bb.removeChips(bigBlind);

        state.addToPot(smallBlind + bigBlind);

        state.setCurrentBet(sb.getId(), smallBlind);
        state.setCurrentBet(bb.getId(), bigBlind);

        state.getTableState().setCurrentBet(bigBlind);
    }

    private void dealCards(GameState state) {
    }

    private void dealFlop(GameState state) {
    }

    private void dealTurn(GameState state) {
    }

    private void dealRiver(GameState state) {
    }

    private void showdown(GameState state) {
    }
}
