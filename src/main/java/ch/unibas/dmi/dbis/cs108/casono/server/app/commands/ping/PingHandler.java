package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/** Handler for {@link PingRequest}. */
public class PingHandler implements CommandHandler<PingRequest> {
    private final ResponseDispatcher responseDispatcher;

    /**
     * Create a new PingHandler to execute {@link PingRequest}s
     *
     * @param responseDispatcher dispatcher used to send responses back to clients
     */
    public PingHandler(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    /**
     * Execute the ping request.
     *
     * @param request the ping request to handle
     */
    @Override
    public void execute(PingRequest request) {
        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
