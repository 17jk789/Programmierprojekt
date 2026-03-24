package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public abstract class Response {
    protected SessionId sessionId;
    protected int requestId;

    public SessionId getSessionId() {
        return sessionId;
    }

    public int getRequestId() {
        return requestId;
    }
}
