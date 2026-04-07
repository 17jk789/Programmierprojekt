package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserId;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

public class LoginResponse extends SuccessResponse {
    public LoginResponse(RequestContext context, String assignedUsername, UserId id) {
        super(
                context,
                new ResponseBodyBuilder()
                        .param("USERNAME", assignedUsername)
                        .param("ID", id.value())
                        .build());
    }
}
