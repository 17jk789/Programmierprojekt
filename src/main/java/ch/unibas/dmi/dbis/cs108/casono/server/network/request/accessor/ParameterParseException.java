package ch.unibas.dmi.dbis.cs108.casono.server.network.request.accessor;

/** Exception thrown when a parameter value cannot be converted to the requested type. */
public class ParameterParseException extends RuntimeException {
    private final String parameterKey;

    /**
     * Creates a new parse exception with a root cause.
     *
     * @param message human-readable description of the parsing failure
     * @param parameterKey key for whose value the error occured
     * @param cause original exception thrown during parsing
     */
    public ParameterParseException(String message, String parameterKey, Throwable cause) {
        super(message, cause);
        this.parameterKey = parameterKey;
    }

    /**
     * Returns the missing parameter key.
     *
     * @return key for whose value the error occured
     */
    public String getParameterKey() {
        return parameterKey;
    }
}
