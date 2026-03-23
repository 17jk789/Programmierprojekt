package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public abstract class SuccessResponse extends Response {
    public SuccessResponse(SessionId sessionId, int requestId) {
        this.sessionId = sessionId;
        this.requestId = requestId;
    }

    public final String encode() {
        String payload = payload();
        if (payload.isBlank()) {
            return "+OK";
        } else {
            return "+OK " + payload;
        }
    }

    protected String payload() {
        return "";
    }
}
