package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

public class MissingParameterException extends RuntimeException {
    private final String parameterKey;

    public MissingParameterException(String message, String parameterKey) {
        super(message);
        this.parameterKey = parameterKey;
    }

    public String getParameterKey() {
        return parameterKey;
    }
}
