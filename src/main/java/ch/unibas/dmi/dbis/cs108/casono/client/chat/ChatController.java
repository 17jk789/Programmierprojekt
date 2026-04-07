package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ChatClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import org.jspecify.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;


/**
 * responsible for the transferring of messages from the server to the ChatModel or from the
 * ChatViewController to the server
 */
public class ChatController {

    private final String username;

    private final ChatClient chatClient;

    public ChatBoxController getChatBoxController() {
        return chatBoxController;
    }

    private final ChatBoxController chatBoxController;
    private int lobbyId = -1;
    private final Timer timer;


    public record ChatKey(ChatType type, @Nullable String targetUser){
        public ChatKey(ChatType type){
            this(type, null);
        }
    }

    public Map<ChatKey, ChatModel> getChatModelMap() {
        return chatModelMap;
    }

    private Map<ChatKey, ChatModel> chatModelMap;

    private static final long REFRESH_TIME = 1000;

    public ChatController(String username, ClientService clientService) {
        this.username = username;
        chatClient = new ChatClient(clientService);
        chatModelMap = new LinkedHashMap<>();
        this.chatBoxController = new ChatBoxController(username, this);
        this.timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        clientService.ping();
                        receiveMessage();
                    }
                },
                0,
                REFRESH_TIME);
    }

    public void setLobbyChat(int lobbyId) {
        this.lobbyId = lobbyId;
        ChatModel lobbyChatModel = new ChatModel(ChatType.LOBBY, username, lobbyId, null);
        chatModelMap.put(new ChatKey(ChatType.LOBBY), lobbyChatModel);
    }

    /** method to get all messages from the server */
    public void receiveMessage() {
        List<Message> newMessages = chatClient.getMessages();
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                switch (msg.getMessageType()) {
                    case ChatType.GLOBAL:
                        chatModelMap.get(new ChatKey(ChatType.GLOBAL)).addMessage(msg);
                    case ChatType.LOBBY:
                        if (msg.lobbyId == lobbyId) {
                            chatModelMap.computeIfAbsent(new ChatKey(ChatType.LOBBY),
                                    (_key) -> new ChatModel(ChatType.LOBBY, username, msg.lobbyId, null)).addMessage(msg);
                        }
                    case ChatType.WHISPER:
                        // TODO: Check, if target person is user and if yes, iterate through all
                        if (msg.target.equals(username)) {
                            if (chatModelMap.containsKey(new ChatKey(ChatType.WHISPER, msg.sender))) {
                                chatModelMap.get(new ChatKey(ChatType.WHISPER, msg.sender)).addMessage(msg);
                            } else {
                                ChatModel value = new ChatModel(ChatType.WHISPER, username, -1, msg.sender);
                                chatModelMap.put(new ChatKey(ChatType.WHISPER, msg.sender), value);
                                chatBoxController.addWhisperChat(msg.sender, value);
                            }
                        }


                }
            }
        }
    }

    /**
     * method to send a message to the server
     * @param message
     */
    public void onSendToNetwork(Message message) {
        chatClient.sendMessage(message);
    }
}
