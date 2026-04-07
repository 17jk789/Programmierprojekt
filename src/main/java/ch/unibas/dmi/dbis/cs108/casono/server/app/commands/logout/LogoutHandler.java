package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/** Handles {@link LogoutRequest} to logout connected user. */
public class LogoutHandler implements CommandHandler<LogoutRequest> {
    private final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    /**
     * Creates a new logout handler to logout user.
     *
     * @param responseDispatcher dispatcher used to send the logout result back to the client
     * @param userRegistry registry responsible for tracking connected user sessions
     */
    public LogoutHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    /**
     * Executes the logout request for the given request.
     *
     * <p>The user is removed from the {@link UserRegistry}.
     *
     * @param request the request to execute
     */
    @Override
    public void execute(LogoutRequest request) {
        boolean wasRemoved = userRegistry.removeBySessionId(request.getSessionId());
        if (wasRemoved) {
            responseDispatcher.dispatch(new OkResponse(request.getContext()));
        } else {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "NO_USER_ASSOCIATED",
                            "No user is associated with your session. Did you login before?"));
        }
    }
}
