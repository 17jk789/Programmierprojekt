package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class GetMessageCountRequest extends Request {
    /**
     * Constructs a new GetMessageCountRequest with the specified request context.
     * This request is used by a client to query the number of pending messages
     * currently waiting in their server-side queue.
     *
     * @param context The {@link RequestContext} associated with this request.
     */
    public GetMessageCountRequest(RequestContext context) {
        super(context);
    }
}
