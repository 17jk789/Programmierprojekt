package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserFactory;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

public class LoginHandler implements CommandHandler<LoginRequest> {
    private final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;
    private final UserFactory userFactory;

    public LoginHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
        this.userFactory = new UserFactory(userRegistry);
    }

    @Override
    public void execute(LoginRequest request) {
        Optional<User> existingUser = userRegistry.getBySessionId(request.getSessionId());
        if (existingUser.isPresent()) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "ALREADY_LOGGED_IN",
                            "This session is already associated with an active user."));
            return;
        }

        User user = userFactory.create(request.getUsername(), request.getSessionId());
        responseDispatcher.dispatch(
                new LoginResponse(request.getContext(), user.getName(), user.getId()));
    }
}
