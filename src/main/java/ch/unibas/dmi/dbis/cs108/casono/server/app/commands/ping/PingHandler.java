package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class PingHandler implements CommandHandler<PingRequest> {
    private final ResponseDispatcher responseDispatcher;

    public PingHandler(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void execute(PingRequest request) {
        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
