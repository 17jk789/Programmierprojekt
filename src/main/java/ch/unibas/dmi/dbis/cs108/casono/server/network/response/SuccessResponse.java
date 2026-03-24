package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public abstract class SuccessResponse extends Response {
    protected SuccessResponse(SessionId sessionId, int requestId, ResponseBody body) {
        super(sessionId, requestId, body);
    }

    @Override
    public final String prefix() {
        return "+OK";
    }
}
