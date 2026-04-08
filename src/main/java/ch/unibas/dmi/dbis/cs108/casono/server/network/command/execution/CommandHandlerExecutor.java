package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/**
 * Invokes execution of the {@link CommandHandler} if all defined pre-execution checks pass.
 *
 * <p>If any check fails, the response returned by the check is dispatched immediately and execution
 * is aborted. Otherwise, the handler will invoke execution normally.
 */
public class CommandHandlerExecutor {
    private final ResponseDispatcher responseDispatcher;

    /**
     * Creates a new CommandHandlerExecutor to execute pre-execution checks and invoke the {@link
     * CommandHandler} if all checks pass.
     *
     * @param responseDispatcher to dispatch the response with if an check fails
     */
    public CommandHandlerExecutor(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    /**
     * Executes a command handler if all defined pre-execution checks pass.
     *
     * @param handler the command handler containing checks and execution logic
     * @param request the incoming request to validate and process
     * @throws NullPointerException if handler or request is null
     */
    public void execute(CommandHandler<Request> handler, Request request) {
        for (HandlerCheck check : handler.getChecks()) {
            Optional<Response> result = check.check(request);
            if (result.isPresent()) {
                responseDispatcher.dispatch(result.get());
                return;
            }
        }

        handler.execute(request);
    }
}
