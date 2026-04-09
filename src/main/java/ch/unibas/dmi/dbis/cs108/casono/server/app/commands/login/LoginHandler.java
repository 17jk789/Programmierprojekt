package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserFactory;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/**
 * Handles {@link LoginRequest}s to create a user for a session, if the session has not assigned one
 * already
 */
public class LoginHandler extends CommandHandler<LoginRequest> {
    private final UserRegistry userRegistry;
    private final UserFactory userFactory;

    /**
     * Creates a new handler for checking for existing user and creating a new one
     *
     * @param responseDispatcher the dispatcher used to send the response
     * @param userRegistry the registry used to look up existing users and create the new one
     */
    public LoginHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
        this.userFactory = new UserFactory(userRegistry);
    }

    /**
     * Executes the login request
     *
     * <p>If no user is already assigned to the session, a new user is created and its name and
     * {@link UserId} returned in the response. If the session has already a user assigned, a {@code
     * ALREADY_LOGGED_IN} error is responded with.
     *
     * @param request the request to execute
     */
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
