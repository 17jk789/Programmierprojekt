package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

public class CheckUsernameHandler implements CommandHandler<CheckUsernameRequest> {
    private final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    public CheckUsernameHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(CheckUsernameRequest request) {
        Optional<User> user = userRegistry.getByUsername(request.getUsername());
        UsernameAvailability availability;
        if (user.isEmpty()) {
            availability = UsernameAvailability.FREE;
        } else {
            availability = UsernameAvailability.TAKEN;
        }
        responseDispatcher.dispatch(new CheckUsernameResponse(request.getContext(), availability));
    }
}
