package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

/**
 * Abstract {@link Response} specialization indicating a successful outcome.
 *
 * <p>Implementations of this class use the {@code +OK} prefix. It provides a protected constructor
 * so subclasses can supply the response body content.
 */
public class SuccessResponse extends Response {
    /**
     * Create a successful response with the provided body.
     *
     * @param context the RequestContext of the request
     * @param body the response body
     */
    public SuccessResponse(RequestContext context, ResponseBody body) {
        super(context, body);
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
