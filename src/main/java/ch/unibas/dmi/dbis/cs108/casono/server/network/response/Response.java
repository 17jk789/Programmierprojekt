package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public abstract class Response {
    private final SessionId sessionId;
    private final int requestId;
    private final ResponseBody body;

    protected Response(SessionId sessionId, int requestId, ResponseBody body) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.body = body;
    }

    public abstract String prefix();

    public SessionId getSessionId() {
        return sessionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public ResponseBody getBody() {
        return body; 
    }
}
