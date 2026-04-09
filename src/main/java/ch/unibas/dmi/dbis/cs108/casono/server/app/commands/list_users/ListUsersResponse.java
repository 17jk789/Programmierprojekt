package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import java.util.Collection;

/** Response containing a list of all active users on the server */
public class ListUsersResponse extends SuccessResponse {
    /**
     * Creates a new ListUsersResponse containing the given list of users
     *
     * @param context the {@link RequestContext} associated with the request
     * @param users the collection of users currently active on the server
     */
    public ListUsersResponse(RequestContext context, Collection<User> users) {
        super(
                context,
                ResponseBody.builder()
                        .block(
                                "USERS",
                                users_block -> {
                                    for (User user : users) {
                                        users_block.block(
                                                "USER",
                                                user_block -> {
                                                    user_block.param("USERNAME", user.getName());
                                                    user_block.param("ID", user.getId().value());
                                                });
                                    }
                                })
                        .build());
    }
}
