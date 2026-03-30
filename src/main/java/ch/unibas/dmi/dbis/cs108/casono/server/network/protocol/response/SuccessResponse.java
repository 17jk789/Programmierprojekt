package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/**
 * Abstract {@link Response} specialization indicating a successful outcome.
 *
 * <p>Implementations of this class use the {@code +OK} prefix. It provides a protected constructor
 * so subclasses can supply the response body content.
 */
public abstract class SuccessResponse extends Response {
    /**
     * Create a successful response with the provided body.
     *
     * @param sessionId the session id this response targets
     * @param requestId the originating request id
     * @param body the response body
     */
    protected SuccessResponse(SessionId sessionId, int requestId, ResponseBody body) {
        super(sessionId, requestId, body);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns the fixed {@code +OK} prefix.
     *
     * @return the {@code +OK} prefix
     */
    @Override
    public final String prefix() {
        return "+OK";
    }
}
