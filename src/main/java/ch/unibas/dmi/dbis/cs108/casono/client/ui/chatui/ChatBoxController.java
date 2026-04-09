package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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

    @FXML
    public void initialize() {
        ChatModel globalChatModel = new ChatModel(ChatType.GLOBAL, username, -1, null);
        chatController
                .getChatModelMap()
                .put(new ChatController.ChatKey(ChatType.GLOBAL), globalChatModel);
        addChatTab("GLOBAL", globalChatModel);
        // TODO: Button to add new Whisper Chat
    }

    public void addWhisperUser(String targetUserName) {
        MenuItem menuItem = new MenuItem(targetUserName);
        whisperUsers.add(menuItem);
        addWhisperChatButton.getItems().add(menuItem);
        menuItem.setOnAction(event -> addWhisperChat(targetUserName));
    }

    public void addWhisperChat(String target) {
        ChatModel chatModel = new ChatModel(ChatType.WHISPER, username, -1, target);
        chatController
                .getChatModelMap()
                .put(new ChatController.ChatKey(ChatType.WHISPER, target), chatModel);
        addChatTab(target, chatModel);
    }

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
