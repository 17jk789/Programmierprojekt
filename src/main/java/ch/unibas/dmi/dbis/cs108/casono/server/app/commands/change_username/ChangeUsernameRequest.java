package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request used to change the username of the current session user. */
public class ChangeUsernameRequest extends Request {
    private final String username;

    /**
     * @param context request context for responses
     * @param username desired new username
     */
    public ChangeUsernameRequest(RequestContext context, String username) {
        super(context);
        this.username = username;
    }

    /**
     * @return desired new username
     */
    public String getUsername() {
        return username;
    }
}
