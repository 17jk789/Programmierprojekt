package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.BetAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The MinimumBetRule class implements the Rule interface and defines the
 * validation logic for ensuring that a player's bet meets the minimum bet
 * requirement in a poker game. It checks if the bet amount is less than the
 * minimum bet (usually determined by the big blind) and throws a
 * RuleViolationException if the bet is below the minimum.
 */
public class MinimumBetRule implements Rule {

    /**
     * Validates the bet action by checking if the bet amount is less than the
     * minimum bet. If it is, a RuleViolationException is thrown, indicating that
     * the bet is below the minimum.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated, which should be an instance of
     *               BetAction.
     * @throws RuleViolationException if the bet amount is below the minimum bet.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (action instanceof BetAction bet) {

            int minBet = state.getTableState().getBigBlind();

            if (bet.getAmount() < minBet) {
                throw new RuleViolationException("Bet below minimum");
            }
        }
    }
}
