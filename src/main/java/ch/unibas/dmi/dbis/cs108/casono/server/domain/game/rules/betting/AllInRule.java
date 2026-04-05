package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.AllInAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The AllInRule class implements the Rule interface and defines the validation logic for the
 * "all-in" action in a poker game. It checks whether the player attempting to go all-in has any
 * chips left, and throws a RuleViolationException if the player has no chips to bet.
 */
public class AllInRule implements Rule {

    /**
     * Validates the "all-in" action by checking if the player has any chips left. If the player has
     * no chips, a RuleViolationException is thrown.
     *
     * @param state The current state of the game.
     * @param action The action to be validated, which should be an instance of AllInAction.
     * @throws RuleViolationException if the player has no chips to go all-in.
     */
    @Override
    public void validate(GameState state, Action action) {

        if (action instanceof AllInAction allIn) {

            Player player = state.getPlayer(allIn.getPlayerId());

            if (player.getChips() <= 0) {
                throw new RuleViolationException("No chips for all-in");
            }
        }
    }
}
