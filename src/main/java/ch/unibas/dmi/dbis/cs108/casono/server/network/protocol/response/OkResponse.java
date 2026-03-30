package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/**
 * A simple success response with an empty body.
 *
 * <p>Use this to acknowledge successful requests that do not carry additional payload data.
 */
public class OkResponse extends SuccessResponse {
    /**
     * Create a minimal successful response (no body content).
     *
     * @param sessionId the target session id
     * @param requestId the originating request id
     */
    public OkResponse(SessionId sessionId, int requestId) {
        super(sessionId, requestId, ResponseBody.builder().build());
    }
}
