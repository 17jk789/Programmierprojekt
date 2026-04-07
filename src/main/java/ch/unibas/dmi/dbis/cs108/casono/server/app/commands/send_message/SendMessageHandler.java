package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;


import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class SendMessageHandler implements CommandHandler<SendMessageRequest> {
    public final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    public SendMessageHandler(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    @Override
    public void execute(SendMessageRequest request) {
        Message message = request.getMessage();
        broadcast(message);
        OkResponse response = new OkResponse(request.getContext());
        responseDispatcher.dispatch(response);
    }
    public void broadcast(Message message) {
        userRegistry.getAllUsers().forEach(user -> user.enqueueMessage(message));
    }

}
