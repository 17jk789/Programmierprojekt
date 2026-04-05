package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The Rule interface defines the structure for all rules in the poker game. Each rule must provide
 * a validate method that checks if a given action is valid based on the current game state. If the
 * action violates the rule, a RuleViolationException should be thrown to indicate the specific rule
 * that was violated.
 */
public interface Rule {
    void validate(GameState state, Action action);
}
