package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class SendMessageHandler extends CommandHandler<SendMessageRequest> {
    private final UserRegistry userRegistry;
    private final LobbyManager lobbyManager;

    /**
     * Constructs a new SendMessageHandler with the required dispatcher and user registry.
     *
     * @param responseDispatcher The dispatcher used to send responses back to clients.
     * @param userRegistry The registry containing all currently connected users.
     */
    public SendMessageHandler(
            ResponseDispatcher responseDispatcher,
            UserRegistry userRegistry,
            LobbyManager lobbyManager) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
        this.lobbyManager = lobbyManager;
    }

    /**
     * Processes a message send request. This method extracts the message from the request,
     * broadcasts it to all connected users, and dispatches a success response (OK) back to the
     * sender.
     *
     * @param request The {@link SendMessageRequest} containing the message and context.
     */
    @Override
    public void execute(SendMessageRequest request) {
        Message message = request.getMessage();
        broadcast(message);
        OkResponse response = new OkResponse(request.getContext());
        responseDispatcher.dispatch(response);
    }

    /**
     * Distributes a message to every user currently registered in the system. Each user's message
     * queue is updated with the new message.
     *
     * @param message The {@link Message} object to be broadcast.
     */
    public void broadcast(Message message) {
        if (message != null
                && message.getMessageType() == ChatType.LOBBY
                && message.lobbyId >= 0
                && lobbyManager != null) {
            lobbyManager.broadcast(
                    LobbyId.of(message.lobbyId),
                    username ->
                            userRegistry
                                    .getByUsername(username)
                                    .ifPresent(user -> user.enqueueMessage(message)));
            return;
        }

        userRegistry.getAllUsers().forEach(user -> user.enqueueMessage(message));
    }
}
