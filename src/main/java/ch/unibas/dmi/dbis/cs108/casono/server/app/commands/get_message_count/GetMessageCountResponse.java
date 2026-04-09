package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

public class GetMessageCountResponse extends SuccessResponse {
    /**
     * Constructs a new GetMessageCountResponse. It creates a response body
     * containing the "COUNT" parameter and associates it with the original
     * request context.
     *
     * @param context The {@link RequestContext} of the original request.
     * @param count The number of pending messages to be returned to the client.
     */
    public GetMessageCountResponse(RequestContext context, int count) {
        super(context, new ResponseBodyBuilder().param("COUNT", count).build());
    }
}
