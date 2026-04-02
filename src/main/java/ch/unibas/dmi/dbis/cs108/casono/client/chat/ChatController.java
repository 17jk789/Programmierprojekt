package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import java.util.ArrayList;
import java.util.List;

/**
 * responsible for the transferring of messages from the server to the ChatModel or from the
 * ChatViewController to the server
 */
public class ChatController {

    private final String username;
    private final ClientService clientService;

    private final ArrayList<ChatModel> chatModelArrayList;

    public ChatController(String username, ClientService clientService) {
        this.username = username;
        this.clientService = clientService;
        chatModelArrayList = new ArrayList<>();
        chatModelArrayList.add(new ChatModel(ChatType.GLOBAL, username));
    }

    public void createLobbyChat(int lobbyId, ChatType chatType) {
        ChatModel chatModel = new ChatModel(chatType, username, lobbyId);
        chatModelArrayList.add(chatModel);
    }

    /** method to get all messages from the server */
    public Boolean receiveMessage() {
        List<Message> newMessages = clientService.getMessages();
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                switch (msg.getMessageType()) {
                    case ChatType.GLOBAL:
                        chatModelArrayList.get(0).addMessage(msg);
                    case ChatType.LOBBY:
                        ChatModel chatModel = chatModelArrayList.get(1);
                        if (chatModel != null && chatModel.lobbyId == msg.lobbyId) {
                            chatModel.addMessage(msg);
                        }
                    case ChatType.WHISPER:
                        // TODO: Check, if target person is user and if yes, iterate through all
                        // whisper chats.
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /** method to send a message to the server */
    public void sendMessageToNetwork(Message message) {
        clientService.sendMessage(message);
    }
}
