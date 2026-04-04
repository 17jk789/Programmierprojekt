package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.ActionType;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The AcceptedActionRule class implements the Rule interface and defines the
 * validation logic for accepted player actions in a poker game. It checks
 * whether the action type is one of the allowed types (FOLD, CALL, RAISE,
 * CHECK, BET, ALL_IN) and throws a RuleViolationException if the action is not
 * allowed.
 */
public class AcceptedActionRule implements Rule {

    /**
     * Validates the given action against the accepted action types for a poker
     * game. If the action type is not one of the allowed types, a
     * RuleViolationException is thrown.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated.
     * @throws RuleViolationException if the action type is not allowed.
     */
    @Override
    public void validate(GameState state, Action action) {

        ActionType type = action.getType();

        switch (type) {
            case FOLD, CALL, RAISE, CHECK, BET, ALL_IN -> {
            }
            default -> throw new RuleViolationException("Action not allowed");
        }
    }
}
