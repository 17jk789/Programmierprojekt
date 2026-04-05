package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.List;

/**
 * The RuleEngine class is responsible for managing and validating a list of rules in the poker
 * game. It provides a method to validate a given action against all the rules, ensuring that the
 * action complies with the game's regulations. If any rule is violated during validation, a
 * RuleViolationException will be thrown, indicating the specific rule that was not followed.
 */
public class RuleEngine {

    private final List<Rule> rules;

    /**
     * Constructs a RuleEngine with the specified list of rules.
     *
     * @param rules The list of rules to be managed by the RuleEngine.
     */
    public RuleEngine(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Validates the given action against all the rules in the RuleEngine. If any rule is violated,
     * a RuleViolationException will be thrown, indicating the specific rule that was not followed.
     *
     * @param state The current state of the game.
     * @param action The action to be validated against the rules.
     * @throws RuleViolationException if any rule is violated during validation.
     */
    public void validate(GameState state, Action action) {
        for (Rule rule : rules) {
            rule.validate(state, action);
        }
    }
}
