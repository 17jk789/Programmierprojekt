package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.ActionType;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The OutOfTurnRule class implements the Rule interface and defines the
 * validation logic for out-of-turn actions in a poker game. It checks whether
 * out-of-turn actions are allowed in the current game state and, if so, whether
 * the action type is permitted (e.g., only fold allowed out of turn). If an
 * out-of-turn action is not allowed or if the action type is not permitted,
 * a RuleViolationException is thrown.
 */
public class OutOfTurnRule implements Rule {

    /**
     * Validates the given action against the rules for out-of-turn actions in a
     * poker game. If out-of-turn actions are not allowed, the method returns
     * without throwing an exception. If out-of-turn actions are allowed, it checks
     * whether the action type is permitted (e.g., only fold allowed out of turn)
     * and
     * throws a RuleViolationException if the action type is not permitted.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated.
     * @throws RuleViolationException if the action is not allowed out of turn or if
     *                                the action type is not permitted.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (!state.isAllowOutOfTurn()) {
            return; // strikt Mode
        }

        if (action.getType() != ActionType.FOLD) {
            throw new RuleViolationException("Only fold allowed out of turn");
        }
    }
}
