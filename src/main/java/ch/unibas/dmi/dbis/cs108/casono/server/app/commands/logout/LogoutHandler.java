package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class LogoutHandler implements CommandHandler<LogoutRequest> {
    private final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    public LogoutHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(LogoutRequest request) {
        boolean was_removed = userRegistry.removeBySessionId(request.getSessionId());
        if (was_removed) {
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
