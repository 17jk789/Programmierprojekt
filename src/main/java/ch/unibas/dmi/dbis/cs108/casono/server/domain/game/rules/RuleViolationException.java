package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules;

/**
 * RuleViolationException is a custom exception that is thrown when a player action violates a
 * specific rule in the poker game. This exception provides a message detailing the nature of the
 * rule violation, allowing for better error handling and user feedback when invalid actions are
 * attempted.
 */
public class RuleViolationException extends RuntimeException {

    /**
     * Constructs a new RuleViolationException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the rule violation.
     */
    public RuleViolationException(String message) {
        super(message);
    }
}
