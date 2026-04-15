package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserId;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

/** Response for successful username changes. */
public class ChangeUsernameResponse extends SuccessResponse {
    /**
     * @param context request context
     * @param username current username after the rename operation
     * @param id user id of renamed user
     */
    public ChangeUsernameResponse(RequestContext context, String username, UserId id) {
        super(
                context,
                new ResponseBodyBuilder()
                        .param("USERNAME", username)
                        .param("ID", id.value())
                        .build());
    }
}
