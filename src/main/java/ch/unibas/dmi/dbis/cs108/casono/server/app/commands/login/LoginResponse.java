package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserId;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

/** Response containing the assigned username and id of said user */
public class LoginResponse extends SuccessResponse {
    /**
     * @param context the {@link RequestContext} associated with the request
     * @param assignedUsername the assigned username to this user
     * @param id of the created user
     */
    public LoginResponse(RequestContext context, String assignedUsername, UserId id) {
        super(
                context,
                new ResponseBodyBuilder()
                        .param("USERNAME", assignedUsername)
                        .param("ID", id.value())
                        .build());
    }
}
