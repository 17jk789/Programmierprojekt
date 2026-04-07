package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request implementation used to logout a currently logged in user */
public class LogoutRequest extends Request {

    /**
     * Constructs a new LogoutRequest with the given context.
     *
     * @param context the {@link RequestContext} containing information for responding to the
     *     request
     */
    public LogoutRequest(RequestContext context) {
        super(context);
    }
}
