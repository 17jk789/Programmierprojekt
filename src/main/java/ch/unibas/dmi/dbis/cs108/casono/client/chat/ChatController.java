package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ChatClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

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

    public record ChatKey(ChatType type, @Nullable String targetUser) {
        public ChatKey(ChatType type) {
            this(type, null);
        }
    }

    public Map<ChatKey, ChatModel> getChatModelMap() {
        return chatModelMap;
    }

    private final Map<ChatKey, ChatModel> chatModelMap;

    private static final long REFRESH_TIME = 1000;

    /** List of all users connected to the server, to safe them locally on the client */
    private final List<String> localUserList;

    private final Logger logger;

    /**
     * Constructor, adds TimerTask to be sent to the server regularly
     *
     * @param username
     * @param clientService
     */
    public ChatController(String username, ClientService clientService) {
        this.username = username;
        chatClient = new ChatClient(clientService);
        chatModelMap = new LinkedHashMap<>();
        localUserList = new ArrayList<>();
        this.chatBoxController = new ChatBoxController(username, this);
        this.logger = LogManager.getLogger(ChatController.class);
        this.timer = new Timer(true);
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            receiveMessage();
                            checkWhisperUsers();
                        } catch (RuntimeException e) {
                            logger.warn("Chat refresh failed: {}", e.getMessage());
                        }
                    }
                },
                0,
                REFRESH_TIME);
    }

    /**
     * Method to be activated, if a lobby has been chosen. It will update the UI and add a new
     * ChatModel to hold the Data for the Lobby Chat
     *
     * @param lobbyId
     */
    public void setLobbyChat(int lobbyId) {
        this.lobbyId = lobbyId;
        ChatKey key = new ChatKey(ChatType.LOBBY);
        if (chatModelMap.containsKey(key)) {
            return;
        }
        ChatModel lobbyChatModel = new ChatModel(ChatType.LOBBY, username, lobbyId, null);
        chatModelMap.put(key, lobbyChatModel);
        this.chatBoxController.addChatTab("Lobby", lobbyChatModel);
    }

    /**
     * Method to process the Messages that got polled by the {@link ChatClient}.
     *
     * <p>Case distinction for every message: - Messages identified with ChatType.LOBBY -> check if
     * they belong to the users lobby. - Messages identified with ChatType.GLOBAL -> check if the
     * target user of that Message is the user, or the message got sent by the user.
     *
     * <p>All Messages get added to a particular {@link ChatModel}, if the checks passed.
     */
    public void receiveMessage() {
        List<Message> newMessages = chatClient.getMessages();
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                switch (msg.getMessageType()) {
                    case ChatType.GLOBAL:
                        chatModelMap.get(new ChatKey(ChatType.GLOBAL)).addMessage(msg);
                        break;
                    case ChatType.LOBBY:
                        if (msg.lobbyId == lobbyId) {
                            chatModelMap
                                    .computeIfAbsent(
                                            new ChatKey(ChatType.LOBBY),
                                            (_key) ->
                                                    new ChatModel(
                                                            ChatType.LOBBY,
                                                            username,
                                                            msg.lobbyId,
                                                            null))
                                    .addMessage(msg);
                        }
                        break;
                    case ChatType.WHISPER:
                        if (msg.target.equals(username) || msg.sender.equals(username)) {
                            ChatKey key;
                            if (msg.target.equals(username)) {
                                key = new ChatKey(ChatType.WHISPER, msg.sender);
                            } else {
                                key = new ChatKey(ChatType.WHISPER, msg.target);
                            }
                            if (chatModelMap.containsKey(key)) {
                                chatModelMap.get(key).addMessage(msg);
                            } else {
                                ChatModel chatModel =
                                        new ChatModel(
                                                ChatType.WHISPER,
                                                username,
                                                lobbyId,
                                                key.targetUser());
                                chatBoxController.addWhisperChat(key.targetUser(), chatModel);
                                chatModel.addMessage(msg);
                            }
                        }
                        break;
                }
            }
        }
    }

    /**
     * Method to process the usernames of other users connected to the server, after they got polled
     * by the {@link ChatClient}.
     *
     * <p>Checks for every one of the usernames, if it is already in the list localUserList and if
     * not adds them. For every new User added in the List, they get added as an option to start a
     * Whisper Chat with.
     */
    public synchronized void checkWhisperUsers() {
        List<String> users = chatClient.getUsers();
        logger.info(users);
        if (!users.isEmpty()) {
            for (String user : users) {
                String value = user.split("\\=")[1];
                logger.info(value);
                addWhisperUser(value);
            }
        }
    }

    /**
     * Registers a whisper target locally and updates the UI if it is a new user.
     *
     * @param user target username
     */
    public synchronized void addWhisperUser(String user) {
        if (user == null || user.isBlank()) {
            return;
        }
        if (!(localUserList.contains(user) || user.equals(username))) {
            localUserList.add(user);
            logger.info("adding new whisper user");
            chatBoxController.addWhisperUser(user);
        }
    }

    /** Stops polling background tasks for this chat controller instance. */
    public void shutdown() {
        timer.cancel();
    }

    /**
     * method to send a message to the server
     *
     * @param message
     */
    public void onSendToNetwork(Message message) {
        chatClient.sendMessage(message);
    }
}
