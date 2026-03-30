package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

/** Exception thrown when a parameter value cannot be converted to the requested type. */
public class ParameterParseException extends RuntimeException {
    /**
     * Creates a new parse exception with a root cause.
     *
     * @param message human-readable description of the parsing failure
     * @param cause original exception thrown during parsing
     */
    public ParameterParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
