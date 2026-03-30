package ch.unibas.dmi.dbis.cs108.casono.server.network.request.accessor;

/** Exception thrown when a required parameter key is not found. */
public class MissingParameterException extends RuntimeException {
    private final String parameterKey;

    /**
     * Creates a new exception for a missing required parameter.
     *
     * @param message human-readable description of the missing parameter
     * @param parameterKey key of the parameter that could not be found
     */
    public MissingParameterException(String message, String parameterKey) {
        super(message);
        this.parameterKey = parameterKey;
    }

    /**
     * Returns the missing parameter key.
     *
     * @return key of the parameter that could not be found
     */
    public String getParameterKey() {
        return parameterKey;
    }
}
