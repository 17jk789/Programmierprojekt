package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import java.util.Optional;

/**
 * Functional interface used by pre-execution checks. Executed before the execute method of the
 * {@link CommandHandler} is called
 */
@FunctionalInterface
public interface HandlerCheck {
    /**
     * Checks that the requirements outlined by this HandlerCheck are fulfilled.
     *
     * @param request to execute the check with
     * @return Optional containing no value if the check was successful. And a {@link ErrorResponse}
     *     if the check failed, the response will be dispatched to the client.
     */
    Optional<Response> check(Request request);
}
