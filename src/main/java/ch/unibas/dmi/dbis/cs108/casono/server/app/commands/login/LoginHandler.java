package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserFactory;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class LoginHandler implements CommandHandler<LoginRequest> {
    private final ResponseDispatcher responseDispatcher;
    private final UserFactory userFactory;

    public LoginHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userFactory = new UserFactory(userRegistry);
    }

    @Override
    public void execute(LoginRequest request) {
        User user = userFactory.create(request.getUsername(), request.getContext().sessionId());
        responseDispatcher.dispatch(
                new LoginResponse(request.getContext(), user.getName(), user.getId()));
    }
}
