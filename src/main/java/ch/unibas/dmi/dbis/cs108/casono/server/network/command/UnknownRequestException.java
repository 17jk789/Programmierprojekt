package ch.unibas.dmi.dbis.cs108.casono.server.network.command;

public class UnknownRequestException extends RuntimeException {
    private final String requestName;

    public UnknownRequestException(String message, String requestName) {
        this.requestName = requestName;
        super(message);
    }

    public String getRequestName() {
        return requestName;
    }
}
