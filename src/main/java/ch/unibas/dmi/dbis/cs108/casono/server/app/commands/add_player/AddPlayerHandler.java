package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.add_player;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/** Handler for the ADD_PLAYER command. */
public class AddPlayerHandler extends CommandHandler<AddPlayerRequest> {
    private final UserRegistry userRegistry;

    public AddPlayerHandler(UserRegistry userRegistry, ResponseDispatcher responseDispatcher) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(AddPlayerRequest request) {
        Optional<User> created =
                userRegistry.registerIfAvailable(
                        request.getName(), request.getContext().sessionId());
        if (created.isEmpty()) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "NAME_TAKEN", "Name already taken"));
            return;
        }

        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
