package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.RaiseAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The BindingDeclarationRule class implements the Rule interface and defines
 * the
 * validation logic for binding declarations in a poker game. It checks whether
 * a raise action is an under-raise or string bet by comparing the raise amount
 * to the current bet commitment of the player. If the raise amount is less than
 * the committed amount, a RuleViolationException is thrown.
 */
public class BindingDeclarationRule implements Rule {

    /**
     * Validates the raise action by checking if the raise amount is less than the
     * current bet commitment of the player. If it is, a RuleViolationException is
     * thrown, indicating an illegal under-raise or string bet.
     *
     * @param state  The current state of the game.
     * @param action The action to be validated, which should be an instance of
     *               RaiseAction.
     * @throws RuleViolationException if the raise amount is less than the current
     *                                bet commitment.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (action instanceof RaiseAction raise) {

            int committed = state.getCurrentBetCommitment(action.getPlayerId());

            if (raise.getAmount() < committed) {
                throw new RuleViolationException("Illegal under-raise / string bet");
            }
        }
    }
}
