package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

public class GetNextMessageResponse extends SuccessResponse {
    /**
     * Constructs a new GetNextMessageResponse. It converts the provided {@link Message}
     * into a network-compatible response body and associates it with the original
     * request context.
     *
     * @param context The {@link RequestContext} of the request being answered.
     * @param msg The {@link Message} to be sent back to the client.
     */
    public GetNextMessageResponse(RequestContext context, Message msg) {
        super(context, msg.toResponse(ResponseBody.builder()));
    }
}
