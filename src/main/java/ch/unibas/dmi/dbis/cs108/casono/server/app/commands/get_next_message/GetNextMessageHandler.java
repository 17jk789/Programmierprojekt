package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

import java.util.Optional;

public class GetNextMessageHandler implements CommandHandler<GetNextMessageRequest> {
    public final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    public GetNextMessageHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(GetNextMessageRequest request) {
        Optional<User> user = userRegistry.getBySessionId(request.getSessionId());
        if (user.isPresent()) {
            Message msg = user.get().dequeMessage();
            GetNextMessageResponse response = new GetNextMessageResponse(
                    request.getContext(),
                    msg
            );
            responseDispatcher.dispatch(response);
        } else {
            ErrorResponse response = new ErrorResponse(
                    request.getContext(),
                    "",
                    "user could not be identified by SessionId"

            );
            responseDispatcher.dispatch(response);
        }
    }
}
