package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class CheckUsernameRequest extends Request {
    private final String username;

    public CheckUsernameRequest(RequestContext context, String username) {
        super(context);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
