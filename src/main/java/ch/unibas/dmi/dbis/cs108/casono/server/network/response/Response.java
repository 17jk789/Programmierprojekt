package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/** Abstract base class for all server responses sent to clients. */
public abstract class Response {
    private final SessionId sessionId;
    private final int requestId;
    private final ResponseBody body;

    /**
     * Create a new {@code Response}.
     *
     * @param sessionId the id of the session this response targets
     * @param requestId the request identifier this response corresponds to
     * @param body the structured response body
     */
    protected Response(SessionId sessionId, int requestId, ResponseBody body) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.body = body;
    }

    /**
     * Returns the protocol prefix for this response (for example {@code "+OK"} or {@code "-ERR"}).
     *
     * @return the response prefix string used by the encoder
     */
    public abstract String prefix();

    /**
     * Returns the session id that should receive this response.
     *
     * @return the target {@link SessionId}
     */
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * Returns the request identifier associated with this response.
     *
     * @return the numeric request id
     */
    public int getRequestId() {
        return requestId;
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
