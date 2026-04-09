package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class ListUsersRequest extends Request {
    public ListUsersRequest(RequestContext context) {
        super(context);
    }
}
