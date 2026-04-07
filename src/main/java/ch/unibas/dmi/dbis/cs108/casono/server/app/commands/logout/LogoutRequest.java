package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class LogoutRequest extends Request {
    public LogoutRequest(RequestContext context) {
        super(context);
    }
}
