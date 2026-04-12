package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.betting;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.Rule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleViolationException;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The ActionOrderRule class implements the Rule interface and defines the validation logic for
 * ensuring that players take their turns in the correct order during a poker game. It checks
 * whether the player performing the action is the current player whose turn it is, and throws a
 * RuleViolationException if the action is attempted out of turn.
 */
public class ActionOrderRule implements Rule {

    /**
     * Validates that the player performing the action is the current player whose turn it is in the
     * game. If the action is attempted by a player who is not the current player, a
     * RuleViolationException is thrown.
     *
     * @param state The current state of the game.
     * @param action The action to be validated.
     * @throws RuleViolationException if the action is attempted out of turn.
     */
    @Override
    public void validate(GameState state, Action action) {

        PlayerId actingPlayerId = action.getPlayerId();

        Player current = state.getCurrentPlayer();

        if (!current.getId().equals(actingPlayerId)) {
            throw new RuleViolationException("Not your turn");
        }
    }
}
