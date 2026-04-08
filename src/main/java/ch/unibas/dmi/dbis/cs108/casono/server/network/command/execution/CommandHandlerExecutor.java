package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

public class CommandHandlerExecutor {
    private final ResponseDispatcher responseDispatcher;

    public CommandHandlerExecutor(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

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
