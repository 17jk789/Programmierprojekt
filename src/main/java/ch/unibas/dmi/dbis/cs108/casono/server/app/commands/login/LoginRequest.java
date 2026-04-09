package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request implementation used to create server-side user instance based on provided username */
public class LoginRequest extends Request {
    private final String username;

    /**
     * Constructs a new LoginRequest with the given context and desired username
     *
     * @param context the {@link RequestContext} containing information for responding to the
     *     request
     * @param username the desired username when creating the user
     */
    public LoginRequest(RequestContext context, String username) {
        super(context);
        this.username = username;
    }

    /**
     * Returns the desired username requested for login
     *
     * @return the desired username
     */
    public String getUsername() {
        return username;
    }
}
