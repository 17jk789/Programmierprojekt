package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class GetMessageCountRequest extends Request {
    public GetMessageCountRequest(RequestContext context) {
        super(context);
    }
}
