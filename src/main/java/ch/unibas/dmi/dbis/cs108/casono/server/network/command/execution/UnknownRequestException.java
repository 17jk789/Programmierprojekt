package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

public class UnknownRequestException extends RuntimeException {
    private final String requestName;

    public UnknownRequestException(String message, String requestName) {
        super(message);
        this.requestName = requestName;
    }

    public String getRequestName() {
        return requestName;
    }
}
