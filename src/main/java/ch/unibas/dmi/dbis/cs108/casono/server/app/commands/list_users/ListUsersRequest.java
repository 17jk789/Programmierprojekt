package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request implementation used to retrieve all existing users from the server. */
public class ListUsersRequest extends Request {
    /**
     * Constructs a new ListUsersRequest with the given context
     *
     * @param context the {@link RequestContext} containing information for responding to the
     *     request
     */
    public ListUsersRequest(RequestContext context) {
        super(context);
    }
}
