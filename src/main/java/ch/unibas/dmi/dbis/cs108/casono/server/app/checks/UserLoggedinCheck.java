package ch.unibas.dmi.dbis.cs108.casono.server.app.checks;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import java.util.Optional;

public class UserLoggedinCheck implements HandlerCheck {
    private final UserRegistry userRegistry;

    public UserLoggedinCheck(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    public Optional<Response> check(Request request) {
        Optional<User> user = userRegistry.getBySessionId(request.getSessionId());
        if (user.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(
                new ErrorResponse(
                        request.getContext(),
                        "USER_NOT_LOGGED_IN",
                        "The execution of this command requires the user to be logged in"));
    }
}
