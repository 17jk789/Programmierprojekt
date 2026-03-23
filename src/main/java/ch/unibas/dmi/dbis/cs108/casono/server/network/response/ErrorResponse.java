package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public class ErrorResponse extends Response {
    private final String code;
    private final String message;

    public ErrorResponse(
            SessionId sessionId, int requestId, String errorCode, String errorMessage) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.code = errorCode;
        this.message = errorMessage;
    }

    public ErrorResponse(SessionId sessionId, int requestId, String errorCode) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.code = errorCode;
        this.message = null;
    }

    @Override
    public String encode() {
        if (message != null && !message.isEmpty()) {
            return "-ERR CODE=" + code + " MSG=" + message;
        } else {
            return "-ERR CODE=" + code;
        }
    }
}
