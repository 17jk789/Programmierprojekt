package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

public class CheckUsernameResponse extends SuccessResponse {
    public CheckUsernameResponse(RequestContext context, UsernameAvailability availability) {
        super(context, new ResponseBodyBuilder().param("STATUS", availability).build());
    }
}
