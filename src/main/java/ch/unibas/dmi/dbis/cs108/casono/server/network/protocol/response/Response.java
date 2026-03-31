package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/** Abstract base class for all server responses sent to clients. */
public abstract class Response {
    private final RequestContext context;
    private final ResponseBody body;

    /**
     * Create a new {@code Response}.
     *
     * @param sessionId the id of the session this response targets
     * @param requestId the request identifier this response corresponds to
     * @param body the structured response body
     */
    protected Response(RequestContext context, ResponseBody body) {
        this.context = context;
        this.body = body;
    }

    /**
     * Returns the protocol prefix for this response (for example {@code "+OK"} or {@code "-ERR"}).
     *
     * @return the response prefix string used by the encoder
     */
    public abstract String prefix();

    /**
     * Returns the session id of the session that should receive this response.
     *
     * @return the target {@link SessionId}
     */
    public SessionId getSessionId() {
        return context.sessionId();
    }

    /**
     * Returns the request identifier associated with this response.
     *
     * @return the numeric request id
     */
    public int getRequestId() {
        return context.requestId();
    }

    /**
     * Returns the immutable {@link ResponseBody} that carries the structured payload for this
     * response.
     *
     * @return the response body
     */
    public ResponseBody getBody() {
        return body;
    }
}
