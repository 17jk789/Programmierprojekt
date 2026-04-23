package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatBoxController {

    private String username;

    private final ChatController chatController;

    @FXML private VBox chatBox;

    @FXML private TabPane chatTabPane;

    @FXML private MenuButton addWhisperChatButton;

    @FXML private HBox menuBox;

    private FXMLLoader fxmlLoader;

    private final List<String> activeWhisperChats;

    @FXML private final Map<String, Tab> usernameTabMap;

    /**
     * Constructor for the ChatBoxController, initializes the necessary fields and data structures
     * for managing chat tabs and whisper chats.
     *
     * @param username The username of the current user.
     * @param chatController The ChatController instance responsible for handling chat logic and
     *     communication with the server.
     */
    public ChatBoxController(String username, ChatController chatController) {
        this.username = username;
        this.chatController = chatController;
        activeWhisperChats = new ArrayList<>();
        usernameTabMap = new HashMap<>();
    }

    public void setUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.username = username.trim();
        }
    }

    /**
     * Initializes the chat interface by creating the global chat model and adding the corresponding
     * "GLOBAL" tab to the interface. It also registers the global chat in the {@link
     * ChatController}'s model map.
     */
    @FXML
    public void initialize() {
        ChatType global = ChatType.GLOBAL;
        ChatModel globalChatModel = new ChatModel(global, username, -1, null);
        chatController
                .getChatModelMap()
                .put(new ChatController.ChatKey(global), globalChatModel);
        addChatTab("GLOBAL", globalChatModel, global);
        addWhisperChatButton.setOnAction(event -> addWhisperChatButton.show());
    }

    /**
     * Adds a specific user to the list of available whisper targets. Creates a new menu item for
     * the user and defines the action to open a private chat tab when selected.
     *
     * @param targetUserName The username of the person to be added to the whisper list.
     */
    public void addWhisperUser(String targetUserName) {
        Platform.runLater(
                () -> {
                    for (MenuItem existing : addWhisperChatButton.getItems()) {
                        if (targetUserName.equals(existing.getText())) {
                            return;
                        }
                    }
                    MenuItem menuItem = new MenuItem(targetUserName);
                    addWhisperChatButton.getItems().add(menuItem);
                    menuItem.setOnAction(
                            event ->
                                    addWhisperChat(
                                            targetUserName,
                                            new ChatModel(
                                                    ChatType.WHISPER,
                                                    username,
                                                    -1,
                                                    targetUserName)));
                });
    }

    public void removeWhisperUser(String targetUserName) {
        Platform.runLater(
                () ->
                        addWhisperChatButton
                                .getItems()
                                .removeIf(item -> targetUserName.equals(item.getText())));
    }

    public void renameWhisperUser(String oldUsername, String newUsername) {
        if (oldUsername == null
                || newUsername == null
                || oldUsername.isBlank()
                || newUsername.isBlank()
                || oldUsername.equals(newUsername)) {
            return;
        }

        Platform.runLater(
                () -> {
                    for (MenuItem item : addWhisperChatButton.getItems()) {
                        if (oldUsername.equals(item.getText())) {
                            item.setText(newUsername);
                            item.setOnAction(
                                    event ->
                                            addWhisperChat(
                                                    newUsername,
                                                    new ChatModel(
                                                            ChatType.WHISPER,
                                                            username,
                                                            -1,
                                                            newUsername)));
                            break;
                        }
                    }

                    Tab tab = usernameTabMap.remove(oldUsername);
                    if (tab != null) {
                        tab.setText(newUsername);
                        usernameTabMap.put(newUsername, tab);
                    }

                    int idx = activeWhisperChats.indexOf(oldUsername);
                    if (idx >= 0) {
                        activeWhisperChats.set(idx, newUsername);
                    }
                });
    }

    /**
     * Creates a new private (whisper) chat model for a specific target user, registers it within
     * the chat system, and opens a new chat tab.
     *
     * @param target The username of the recipient for the private messages.
     */
    public void addWhisperChat(String target, ChatModel chatModel) {
        if (!activeWhisperChats.contains(target)) {
            activeWhisperChats.add(target);
            ChatType whisper = ChatType.WHISPER;
            chatController
                    .getChatModelMap()
                    .put(new ChatController.ChatKey(whisper, target), chatModel);
            addChatTab(target, chatModel, whisper);
        } else {
            chatTabPane.getSelectionModel().select(usernameTabMap.get(target));
        }
    }

    /**
     * Dynamically loads a new chat tab from an FXML resource and attaches it to the TabPane. It
     * initializes a {@link ChatViewController} for the tab and sets up a listener to display
     * incoming messages in real-time.
     *
     * @param title The title to be displayed on the tab header.
     * @param chatModel The {@link ChatModel} containing the data and logic for this specific chat.
     * @throws RuntimeException If the FXML resource for the chat tab cannot be loaded.
     */
    public void addChatTab(String title, ChatModel chatModel, ChatType chatType) {
        String ressource = "/ui-structure/components/chatui/chattab.fxml";
        URL resource = getClass().getResource(ressource);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        runOnPlatformSynchronized(
                () -> {
                    try {
                        ChatViewController chatViewController =
                                new ChatViewController(
                                        this.chatController, chatModel, this.username);
                        chatController.activeChatControllers.put(
                                new ChatController.ChatKey(chatType), chatViewController);
                        fxmlLoader.setController(chatViewController);
                        Node load = fxmlLoader.load();
                        VBox.setVgrow(load, Priority.ALWAYS);
                        VBox vbox = new VBox();
                        VBox.setVgrow(vbox, Priority.ALWAYS);
                        vbox.getChildren().add(load);
                        chatModel.addListener((msg) -> chatViewController.showMessage(msg));
                        Tab newChat = new Tab(title, vbox);
                        usernameTabMap.put(title, newChat);
                        this.chatTabPane.getTabs().add(newChat);
                        this.chatTabPane.getSelectionModel().select(newChat);
                        return newChat;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Helper Function to cope with a Threads problem occurring when the main JavaFX Thread performs
     * the task to add a new whisper Chat tab, and the task is specified to runLater.
     */
    public static <T> T runOnPlatformSynchronized(Callable<T> code) {
        if (Platform.isFxApplicationThread()) {
            try {
                return code.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            CompletableFuture<T> futureLock = new CompletableFuture<>();
            Platform.runLater(
                    () -> {
                        try {
                            futureLock.complete(code.call());
                        } catch (Exception e) {
                            futureLock.completeExceptionally(e);
                        }
                    });
            try {
                return futureLock.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadChats() {
        Map<ChatController.ChatKey, ChatModel> chatModelMap = chatController.getChatModelMap();
        ChatController.ChatKey key = new ChatController.ChatKey(ChatType.GLOBAL);
        ChatModel global = chatModelMap.get(key);
        ChatViewController globalController = chatController.activeChatControllers.get(key);

        for (String user : chatController.getLocalUserList()) {
            addWhisperUser(user);
        }

        for (Message msg : global.messages) {
            globalController.showMessage(msg);
        }
        for (String user : chatController.getLocalUserList()) {
            ChatController.ChatKey whisperUserKey =
                    new ChatController.ChatKey(ChatType.WHISPER, user);
            if (chatModelMap.containsKey(whisperUserKey)) {
                ChatModel whisperChatModel = chatModelMap.get(whisperUserKey);
                addWhisperChat(user, whisperChatModel);
                for (Message msg : whisperChatModel.messages) {
                    chatController.activeChatControllers.get(whisperUserKey).showMessage(msg);
                }
            }
        }
    }
}
