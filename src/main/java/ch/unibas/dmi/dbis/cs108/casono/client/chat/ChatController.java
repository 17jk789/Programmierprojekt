package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import java.util.List;

/**
 * responsible for the transferring of messages from the server to the ChatModel
 * or from the ChatViewController to the server
 */
public class ChatController {

    private final String username;
    private final ClientService clientService;

    private ChatModel chatModel;

    private int game_id;

    public ChatController(String username, ClientService clientService) {
        this.username = username;
        this.clientService = clientService;
    }

    public void createChat(int game_id) {
        chatModel = new ChatModel(ChatModel.ChatType.GLOBAL, username);
        this.game_id = game_id;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    /**
     * method to send a message, the ChatViewController received to the server
     */
    public void sendMessage(String msg, String username) {
        Message message = new Message(Message.MessageType.GLOBAL, 0, username, null, msg);
        onSendToNetwork(message);
    }

    /**
     * method to get all messages from the server
     */
    public Boolean receiveMessage() {
        List<Message> newMessages = clientService.getMessages();
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                chatModel.addMessage(msg);
            }
            return true;
        } else { return false; }
    }

    /**
     * method to send a message to the server
     * @param message
     */
    public void onSendToNetwork(Message message) {
        clientService.sendMessage(message);
    }

}
