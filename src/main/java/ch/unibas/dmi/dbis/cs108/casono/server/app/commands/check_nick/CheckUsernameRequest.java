package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
/** Request implementation used to check whether a username is available or already taken */
public class CheckUsernameRequest extends Request {
    private final String username;
    /**
     * Constructs a new CheckUsernameRequest with the given context and username to check
     *
     * @param context the {@link RequestContext} containing information for responding to the
     *     request
     * @param username the username to check for availability
     */
    public CheckUsernameRequest(RequestContext context, String username) {
        super(context);
        this.username = username;
    }
    /**
     * Returns the provided username in the request
     *
     * @return username to check
     */
    public String getUsername() {
        return username;
    }
}