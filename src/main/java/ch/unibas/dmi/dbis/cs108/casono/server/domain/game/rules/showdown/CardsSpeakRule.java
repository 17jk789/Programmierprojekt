package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.showdown;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandEvaluator;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandRank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerStatus;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 * The CardsSpeakRule class implements the Rule interface and defines the logic
 * for
 * determining the winner of a poker hand based on the players' hole cards and
 * the community cards. It evaluates each player's hand, compares their ranks,
 * and awards the pot to the player with the best hand.
 */
public class CardsSpeakRule implements Rule {

    /**
     * Validates the action for the showdown phase. In this implementation, the
     * CardsSpeakRule does not perform any validation, as it is responsible for
     * determining the winner based on the players' hands. The validation logic for
     * player actions during the showdown phase should be handled by other rules.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated.
     */
    @Override
    public void validate(GameState state, Action action) {
        // No validation needed; rule is only applied at showdown
    }

    /**
     * Determines the winner of the poker hand by evaluating each player's hand
     * rank based on their hole cards and the community cards. It compares the
     * hand ranks of all active players and returns the player with the best hand.
     *
     * @param state The current state of the game, which includes player
     *              information,
     *              hole cards, and community cards.
     * @return The player with the best hand, or null if there are no active
     *         players.
     */
    public Player determineWinner(GameState state) {

        List<Player> players = new ArrayList<>(state.getPlayers());

        Player bestPlayer = null;
        HandRank bestRank = null;

        for (Player player : players) {

            if (player.getStatus() == PlayerStatus.FOLDED) {
                continue;
            }

            List<Card> cards = new ArrayList<>();

            cards.addAll(state.getHoleCards(player.getId()));
            cards.addAll(state.getCommunityCards());

            HandRank rank = HandEvaluator.evaluate(cards);

            if (bestRank == null || rank.compareTo(bestRank) > 0) {

                bestRank = rank;
                bestPlayer = player;
            }
        }

        return bestPlayer;
    }

    /**
     * Awards the pot to the winner of the poker hand. It determines the winner(s)
     * using the determineWinner method, retrieves the total amount in the pot,
     * and adds it to the winner's chips. Finally, it resets the pot for the next
     * hand.
     *
     * @param state The current state of the game, which includes player information
     *              and pot details.
     */
    public void awardPot(GameState state) {

        Player winner = determineWinner(state);

        if (winner == null) {
            return;
        }

        int pot = state.getPot().getAmount();

        winner.addChips(pot);

        state.getPot().reset();
    }
}
