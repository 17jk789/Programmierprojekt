package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

public class GetMessageCountHandler extends CommandHandler<GetMessageCountRequest> {
    private final UserRegistry userRegistry;

    public GetMessageCountHandler(
            ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(GetMessageCountRequest request) {
        Optional<User> user = userRegistry.getBySessionId(request.getSessionId());
        if (user.isPresent()) {
            int count = user.get().getMessageCount();
            GetMessageCountResponse response =
                    new GetMessageCountResponse(request.getContext(), count);
            responseDispatcher.dispatch(response);
        } else {
            ErrorResponse response =
                    new ErrorResponse(
                            request.getContext(), "", "user could not be identified by SessionId");

            responseDispatcher.dispatch(response);
        }
    }
}
