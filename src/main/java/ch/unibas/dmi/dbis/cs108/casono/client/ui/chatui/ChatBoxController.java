package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ChatBoxController {

    private String username;

    private ChatController chatController;

    @FXML private VBox chatBox;

    @FXML private TabPane chatTabPane;

    @FXML private MenuButton addWhisperChatButton;

    private FXMLLoader fxmlLoader;

    @FXML private List<MenuItem> whisperUsers;

    private String ressource = "/ui-structure/components/chatui/chattab.fxml";

    public ChatBoxController(String username, ChatController chatController) {
        this.username = username;
        this.chatController = chatController;
    }

    /**
     * Initializes the chat interface by creating the global chat model and
     * adding the corresponding "GLOBAL" tab to the interface.
     * It also registers the global chat in the {@link ChatController}'s model map.
     */
    @FXML
    public void initialize() {
        ChatModel globalChatModel = new ChatModel(ChatType.GLOBAL, username, -1, null);
        chatController
                .getChatModelMap()
                .put(new ChatController.ChatKey(ChatType.GLOBAL), globalChatModel);
        addChatTab("GLOBAL", globalChatModel);
        // TODO: Button to add new Whisper Chat
    }

    /**
     * Adds a specific user to the list of available whisper targets.
     * Creates a new menu item for the user and defines the action to open
     * a private chat tab when selected.
     *
     * @param targetUserName The username of the person to be added to the whisper list.
     */
    public void addWhisperUser(String targetUserName) {
        MenuItem menuItem = new MenuItem(targetUserName);
        whisperUsers.add(menuItem);
        addWhisperChatButton.getItems().add(menuItem);
        menuItem.setOnAction(event -> addWhisperChat(targetUserName));
    }

    /**
     * Creates a new private (whisper) chat model for a specific target user,
     * registers it within the chat system, and opens a new chat tab.
     *
     * @param target The username of the recipient for the private messages.
     */
    public void addWhisperChat(String target) {
        ChatModel chatModel = new ChatModel(ChatType.WHISPER, username, -1, target);
        chatController
                .getChatModelMap()
                .put(new ChatController.ChatKey(ChatType.WHISPER, target), chatModel);
        addChatTab(target, chatModel);
    }

    /**
     * Dynamically loads a new chat tab from an FXML resource and attaches it
     * to the TabPane. It initializes a {@link ChatViewController} for the tab
     * and sets up a listener to display incoming messages in real-time.
     *
     * @param title The title to be displayed on the tab header.
     * @param chatModel The {@link ChatModel} containing the data and logic for this specific chat.
     * @throws RuntimeException If the FXML resource for the chat tab cannot be loaded.
     */
    public void addChatTab(String title, ChatModel chatModel) {
        URL resource = getClass().getResource(ressource);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        try {
            ChatViewController chatViewController =
                    new ChatViewController(this.chatController, chatModel, this.username);
            fxmlLoader.setController(chatViewController);
            Node load = fxmlLoader.load();
            VBox.setVgrow(load, Priority.ALWAYS);
            VBox vbox = new VBox();
            VBox.setVgrow(vbox, Priority.ALWAYS);
            vbox.getChildren().add(load);
            chatModel.addListener((msg) -> chatViewController.showMessage(msg));
            Tab newChat = new Tab(title, vbox);
            this.chatTabPane.getTabs().add(newChat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
