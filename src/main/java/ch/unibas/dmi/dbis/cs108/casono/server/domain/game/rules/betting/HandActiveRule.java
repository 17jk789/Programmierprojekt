package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The HandActiveRule class implements the Rule interface and defines the validation logic for
 * checking if there is an active hand in a poker game. It ensures that players can only perform
 * actions when there is an active hand, and throws a RuleViolationException if there is no active
 * hand.
 */
public class HandActiveRule implements Rule {

    /**
     * Validates that there is an active hand in the game. If there is no active hand, a
     * RuleViolationException is thrown, indicating that players cannot perform actions without an
     * active hand.
     *
     * @param state The current state of the game.
     * @param action The action to be validated.
     * @throws RuleViolationException if there is no active hand in the game.
     */
    @Override
    public void validate(GameState state, Action action) {
        if (!state.isHandActive()) {
            throw new RuleViolationException("No active hand");
        }
    }
}
