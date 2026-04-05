package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

/** Response indicating the availability status of a username check. */
public class CheckUsernameResponse extends SuccessResponse {
    /**
     * Creates a new response to respond to the username availability check to
     *
     * @param context the {@link RequestContext} associated with the request
     * @param availability the availability status of the requested username
     */
    public CheckUsernameResponse(RequestContext context, UsernameAvailability availability) {
        super(context, new ResponseBodyBuilder().param("STATUS", availability).build());
    }
}
