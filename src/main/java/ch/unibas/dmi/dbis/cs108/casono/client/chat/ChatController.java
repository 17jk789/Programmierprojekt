package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ChatClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatViewController;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/**
 * responsible for the transferring of messages from the server to the ChatModel or from the
 * ChatViewController to the server
 */
public class ChatController {

    /** Ensures one active message poller per physical ClientService connection. */
    private static final Map<ClientService, ChatController> ACTIVE_CONTROLLERS =
            new WeakHashMap<>();

    private volatile String username;

    private final ClientService clientService;

    private final ChatClient chatClient;

    public ChatBoxController getChatBoxController() {
        return chatBoxController;
    }

    public void setChatBoxController(ChatBoxController chatBoxController) {this.chatBoxController = chatBoxController; }

    private ChatBoxController chatBoxController;
    private int lobbyId = -1;
    private final Timer timer;
    private final Consumer<List<String>> serverEventListener;

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

    public List<String> getLocalUserList() {
        return this.localUserList;
    }

    public final Map<ChatController.ChatKey, ChatViewController> activeChatControllers;

    private final Logger logger;

    /**
     * Constructor, adds TimerTask to be sent to the server regularly
     *
     * @param username
     * @param clientService
     */
    public ChatController(String username, ClientService clientService) {
        this.username = username;
        this.clientService = clientService;
        chatClient = new ChatClient(clientService);
        chatModelMap = new LinkedHashMap<>();
        localUserList = new ArrayList<>();
        this.chatBoxController = new ChatBoxController(username, this);
        this.logger = LogManager.getLogger(ChatController.class);
        this.serverEventListener = this::handleServerEvent;
        this.activeChatControllers = new HashMap<>();

        registerAsActiveController(clientService);
        clientService.addEventListener(serverEventListener);

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

    private void registerAsActiveController(ClientService clientService) {
        synchronized (ACTIVE_CONTROLLERS) {
            ChatController oldController = ACTIVE_CONTROLLERS.get(clientService);
            if (oldController != null && oldController != this) {
                oldController.shutdown();
                logger.info("Replaced previous ChatController poller for shared ClientService");
            }
            ACTIVE_CONTROLLERS.put(clientService, this);
        }
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
        ChatModel existingModel = chatModelMap.get(key);
        if (existingModel != null) {
            existingModel.lobbyId = lobbyId;
            return;
        }
        ChatModel lobbyChatModel = new ChatModel(ChatType.LOBBY, username, lobbyId, null);
        chatModelMap.put(key, lobbyChatModel);
        this.chatBoxController.addChatTab("LOBBY", lobbyChatModel, ChatType.LOBBY);
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
        String currentUsername = getCurrentUsername();
        List<Message> newMessages = chatClient.getMessages();
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                switch (msg.getMessageType()) {
                    case ChatType.GLOBAL:
                        chatModelMap.get(new ChatKey(ChatType.GLOBAL)).addMessage(msg);
                        break;
                    case ChatType.LOBBY:
                        if (msg.lobbyId == getActiveLobbyChatId()) {
                            chatModelMap
                                    .computeIfAbsent(
                                            new ChatKey(ChatType.LOBBY),
                                            (_key) ->
                                                    new ChatModel(
                                                            ChatType.LOBBY,
                                                            currentUsername,
                                                            msg.lobbyId,
                                                            null))
                                    .addMessage(msg);
                        }
                        break;
                    case ChatType.WHISPER:
                        if (msg.target.equals(currentUsername)
                                || msg.sender.equals(currentUsername)) {
                            ChatKey key;
                            if (msg.target.equals(currentUsername)) {
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
                                                currentUsername,
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
     * Method to get the lobbyId of the currently active lobby chat, to check if incoming lobby
     * messages belong to the same lobby.
     *
     * @return the lobbyId of the currently active lobby chat.
     */
    private int getActiveLobbyChatId() {
        ChatModel lobbyModel = chatModelMap.get(new ChatKey(ChatType.LOBBY));
        if (lobbyModel != null) {
            return lobbyModel.lobbyId;
        }
        return lobbyId;
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
        Set<String> remoteUsers = new HashSet<>();
        String currentUsername = getCurrentUsername();

        for (String user : users) {
            if (user == null || !user.contains("=")) {
                continue;
            }
            String value = user.split("\\=", 2)[1].trim();
            if (value.isBlank() || value.equals(currentUsername)) {
                continue;
            }
            remoteUsers.add(value);
        }

        for (String known : new ArrayList<>(localUserList)) {
            if (!remoteUsers.contains(known)) {
                localUserList.remove(known);
                chatBoxController.removeWhisperUser(known);
            }
        }

        for (String remote : remoteUsers) {
            addWhisperUser(remote);
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
        if (!(localUserList.contains(user) || user.equals(getCurrentUsername()))) {
            localUserList.add(user);
            logger.info("adding new whisper user");
            chatBoxController.addWhisperUser(user);
        }
    }

    public synchronized void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            return;
        }
        String oldUsername = this.username;
        this.username = newUsername.trim();
        chatBoxController.setUsername(this.username);

        if (oldUsername != null && !oldUsername.equals(this.username)) {
            localUserList.remove(oldUsername);
            chatBoxController.removeWhisperUser(oldUsername);
        }
    }

    private void handleServerEvent(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        List<RequestParameter> params;
        try {
            params = ClientService.convertToRequestParameters(lines);
        } catch (RuntimeException e) {
            return;
        }
        String event = null;
        String oldUsername = null;
        String newUsername = null;

        for (RequestParameter p : params) {
            if ("EVENT".equalsIgnoreCase(p.key())) {
                event = p.value();
            } else if ("OLD_USERNAME".equalsIgnoreCase(p.key())) {
                oldUsername = p.value();
            } else if ("NEW_USERNAME".equalsIgnoreCase(p.key())) {
                newUsername = p.value();
            }
        }

        if (!"USERNAME_CHANGED".equalsIgnoreCase(event)) {
            return;
        }

        applyUsernameMigration(oldUsername, newUsername);
    }

    private synchronized void applyUsernameMigration(String oldUsername, String newUsername) {
        if (oldUsername == null
                || newUsername == null
                || oldUsername.isBlank()
                || newUsername.isBlank()
                || oldUsername.equals(newUsername)) {
            return;
        }

        String currentUsername = getCurrentUsername();
        if (oldUsername.equals(currentUsername)) {
            updateUsername(newUsername);
        }

        ChatKey oldKey = new ChatKey(ChatType.WHISPER, oldUsername);
        ChatKey newKey = new ChatKey(ChatType.WHISPER, newUsername);

        ChatModel oldModel = chatModelMap.remove(oldKey);
        ChatModel existingNewModel = chatModelMap.get(newKey);
        if (oldModel != null) {
            oldModel.setTarget(newUsername);
            if (existingNewModel == null) {
                chatModelMap.put(newKey, oldModel);
            } else {
                // Merge possible parallel history into the already existing new-key model.
                for (Message msg : oldModel.messages) {
                    existingNewModel.addMessage(msg);
                }
            }
            chatBoxController.renameWhisperUser(oldUsername, newUsername);
        }

        localUserList.remove(oldUsername);
        chatBoxController.removeWhisperUser(oldUsername);
        if (!newUsername.equals(getCurrentUsername())) {
            addWhisperUser(newUsername);
        }
    }

    public String getCurrentUsername() {
        String shared = ClientApp.getSharedUsername();
        if (shared != null && !shared.isBlank()) {
            return shared.trim();
        }
        return username;
    }

    /** Stops polling background tasks for this chat controller instance. */
    public void shutdown() {
        timer.cancel();
        clientService.removeEventListener(serverEventListener);
        synchronized (ACTIVE_CONTROLLERS) {
            if (ACTIVE_CONTROLLERS.get(clientService) == this) {
                ACTIVE_CONTROLLERS.remove(clientService);
            }
        }
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
