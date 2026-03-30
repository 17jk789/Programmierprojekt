package ch.unibas.dmi.dbis.cs108.casono.server.network.request;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/** Request, produced by the CommandParser */
public abstract class Request {
    protected final RequestContext context;

    public Request(RequestContext context) {
        this.context = context;
    }

    public RequestContext getContext() {
        return context;
    }

    public SessionId getSessionId() {
        return context.sessionId();
    }

    public int getRequestId() {
        return context.requestId();
    }
}
