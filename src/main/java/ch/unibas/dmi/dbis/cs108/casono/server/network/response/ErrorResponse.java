package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/** Response representing an error outcome for a client's request. */
public class ErrorResponse extends Response {
    /**
     * Construct an error response with a code and message.
     *
     * @param sessionId the target session id
     * @param requestId the originating request id
     * @param errorCode a short error code identifying the failure
     * @param errorMessage a human readable error message
     */
    public ErrorResponse(
            SessionId sessionId, int requestId, String errorCode, String errorMessage) {
        super(
                sessionId,
                requestId,
                ResponseBody.builder().param("CODE", errorCode).param("MSG", errorMessage).build());
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns the fixed {@code -ERR} prefix.
     *
     * @return the {@code -ERR} prefix
     */
    @Override
    public String prefix() {
        return "-ERR";
    }
}
