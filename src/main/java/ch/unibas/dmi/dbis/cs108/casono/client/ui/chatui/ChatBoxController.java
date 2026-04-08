package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class ChatBoxController {

    private String username;

    private ChatController chatController;

    @FXML private VBox chatBox;

    @FXML private TabPane ChatTabPane;

    private FXMLLoader fxmlLoader;

    private String ressource =  "/ui-structure/components/chatui/chattab.fxml";


    public ChatBoxController(String username, ChatController chatController) {
        this.username = username;
        this.chatController = chatController;
    }

    @FXML
    public void initialize() {
        ChatModel globalChatModel = new ChatModel(ChatType.GLOBAL, username, -1, null);
        chatController.getChatModelMap().put(new ChatController.ChatKey(ChatType.GLOBAL), globalChatModel);
        addChatTab("GLOBAL", globalChatModel);
        //TODO: Button to add new Whisper Chat
    }


    public void addWhisperChat(String target, ChatModel chatModel) {
        chatController.getChatModelMap().put(new ChatController.ChatKey(ChatType.WHISPER, target), chatModel);
        addChatTab(target, chatModel);
    }


    public void addChatTab(String title, ChatModel chatModel) {
        URL resource = getClass().getResource(ressource);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        ChatViewController chatViewController = new ChatViewController(username, chatModel, chatController);
        chatModel.addListener((msg)-> chatViewController.showMessage(msg));
        try {
            fxmlLoader.setController(chatViewController);
            Tab newChat = new Tab(title, fxmlLoader.load());
            this.ChatTabPane.getTabs().add(newChat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
