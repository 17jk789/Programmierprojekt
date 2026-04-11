package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/** Handles {@link CheckUsernameRequest}s to check whether a username is available. */
public class CheckUsernameHandler extends CommandHandler<CheckUsernameRequest> {
    private final UserRegistry userRegistry;

    /**
     * Creates a new handler for checking username availability.
     *
     * @param responseDispatcher the dispatcher used to send the response
     * @param userRegistry the registry used to look up existing users
     */
    public CheckUsernameHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
    }

    /**
     * Executes the username availability check for the given request.
     *
     * <p>If no user exists for the requested username, the username is reported as {@link
     * UsernameAvailability#FREE}; otherwise, it is reported as {@link UsernameAvailability#TAKEN}.
     *
     * @param request the request to execute
     */
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
