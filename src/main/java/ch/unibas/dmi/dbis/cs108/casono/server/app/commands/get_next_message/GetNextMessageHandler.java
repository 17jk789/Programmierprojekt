package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

import java.util.Optional;

public class GetNextMessageHandler extends CommandHandler<GetNextMessageRequest> {
    private final UserRegistry userRegistry;

    /**
     * Constructs a new GetNextMessageHandler with the required dispatcher and user registry.
     *
     * @param responseDispatcher The dispatcher used to send responses back to clients.
     * @param userRegistry The registry used to look up users by their session information.
     */
    public GetNextMessageHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }
    /**
     * Executes the request to retrieve the next message for a specific user.
     * It identifies the user via their session ID, dequeues the next available message,
     * and dispatches a {@link GetNextMessageResponse}. If the user cannot be identified,
     * an {@link ErrorResponse} is sent instead.
     *
     * @param request The {@link GetNextMessageRequest} containing the session and context.
     */
    @Override
    public void execute(GetNextMessageRequest request) {
        Optional<User> user = userRegistry.getBySessionId(request.getSessionId());
        if (user.isPresent()) {
            Message msg = user.get().dequeMessage();
            GetNextMessageResponse response = new GetNextMessageResponse(request.getContext(), msg);
            responseDispatcher.dispatch(response);
        } else {
            ErrorResponse response =
                    new ErrorResponse(
                            request.getContext(),
                            "NO_USER_ASSOCIATED",
                            "user could not be identified by SessionId");
            responseDispatcher.dispatch(response);
        }
    }
}
