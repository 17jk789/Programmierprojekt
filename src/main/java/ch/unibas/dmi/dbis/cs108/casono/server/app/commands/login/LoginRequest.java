package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class LoginRequest extends Request {
    private final String username;

    public LoginRequest(RequestContext context, String username) {
        super(context);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
