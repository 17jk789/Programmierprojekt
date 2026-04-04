package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.RaiseAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The RaiseReopenRule class implements the Rule interface and defines the
 * validation logic for allowing raises to reopen betting in a poker game. It
 * checks if the action is a RaiseAction and if the betting is open for raising.
 * If betting is not open or if reopening betting is not allowed, it throws a
 * RuleViolationException with an appropriate message.
 */
public class RaiseReopenRule implements Rule {

    /**
     * Validates the raise action by checking if the betting is open for raising and
     * if reopening betting is allowed. If betting is not open, a
     * RuleViolationException
     * is thrown with the message "Betting not open for raise". If reopening betting
     * is not allowed, a RuleViolationException is thrown with the message "Raise
     * not
     * allowed anymore".
     *
     * @param state  The current state of the game.
     * @param action The action to be validated, which should be an instance of
     *               RaiseAction.
     * @throws RuleViolationException if betting is not open for raising or if
     *                                reopening betting is not allowed.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (!(action instanceof RaiseAction)) {
            return;
        }

        if (!state.getTableState().isBettingOpen()) {
            throw new RuleViolationException("Betting not open for raise");
        }

        if (!state.getTableState().canReopenBetting()) {
            throw new RuleViolationException("Raise not allowed anymore");
        }
    }
}
