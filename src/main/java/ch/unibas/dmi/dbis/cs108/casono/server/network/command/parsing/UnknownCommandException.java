package ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing;

/**
 * Exception thrown when the CommandParserDispatcher has no registered handler for the provided
 * request
 */
public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException(String message) {
        super(message);
    }
}
