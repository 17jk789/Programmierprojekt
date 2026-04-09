package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class GetNextMessageRequest extends Request {
    /**
     * Constructs a new GetNextMessageRequest with the specified request context.
     * This request is typically used by a client to poll or retrieve the next
     * available message from the server's queue.
     *
     * @param context The {@link RequestContext} associated with this request.
     */
    public GetNextMessageRequest(RequestContext context) {
        super(context);
    }
}
