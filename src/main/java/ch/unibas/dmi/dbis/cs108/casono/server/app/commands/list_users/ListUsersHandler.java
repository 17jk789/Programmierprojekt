package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Collection;

public class ListUsersHandler extends CommandHandler<ListUsersRequest> {
    private final UserRegistry userRegistry;

    public ListUsersHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(ListUsersRequest request) {
        Collection<User> users = userRegistry.getAllUsers();
        responseDispatcher.dispatch(new ListUsersResponse(request.getContext(), users));
    }
}
