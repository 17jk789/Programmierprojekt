package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.RaiseAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The MinimumRaiseRule class implements the Rule interface and defines the
 * validation logic for ensuring that a raise action in a poker game meets the
 * minimum raise requirement. It checks if the amount of the raise is less than
 * the minimum raise defined in the game state, and if so, it throws a
 * RuleViolationException indicating that the raise is too small.
 */
public class MinimumRaiseRule implements Rule {

    /**
     * Validates the raise action by checking if the raise amount is less than the
     * minimum raise defined in the game state. If it is, a RuleViolationException
     * is thrown, indicating that the raise is too small.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated, which should be an instance of
     *               RaiseAction.
     * @throws RuleViolationException if the raise amount is less than the minimum
     *                                raise.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (!(action instanceof RaiseAction raise)) {
            return;
        }

        int minRaise = state.getTableState().getMinRaise();
        int amount = raise.getAmount();

        if (amount < minRaise) {
            throw new RuleViolationException(
                    "Raise too small. Min is " + minRaise);
        }
    }
}
