package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Collection;

/**
 * Handles {@link ListUsersRequest}s by retrieving all users from the {@link UserRegistry} and
 * dispatching a {@link ListUsersResponse} containing the list of users.
 */
public class ListUsersHandler extends CommandHandler<ListUsersRequest> {
    private final UserRegistry userRegistry;

    /**
     * Creates a new handler for listing all users
     *
     * @param responseDispatcher the dispatcher used to send the response
     * @param userRegistry the registry used to look up existing users
     */
    public ListUsersHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }

    /**
     * Executes the list users request
     *
     * <p>All users are retrieved from the {@link UserRegistry} and returned in a {@link
     * ListUsersResponse}.
     *
     * @param request the request to execute
     */
    @Override
    public void execute(ListUsersRequest request) {
        Collection<User> users = userRegistry.getAllUsers();
        responseDispatcher.dispatch(new ListUsersResponse(request.getContext(), users));
    }
}
