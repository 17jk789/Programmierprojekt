package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

/**
 * A simple success response with an empty body.
 *
 * <p>Use this to acknowledge successful requests that do not carry additional payload data.
 */
public class OkResponse extends SuccessResponse {
    /**
     * Create a minimal successful response (no body content).
     *
     * @param context the RequestContext of the request
     */
    public OkResponse(RequestContext context) {
        super(context, ResponseBody.builder().build());
    }
}
