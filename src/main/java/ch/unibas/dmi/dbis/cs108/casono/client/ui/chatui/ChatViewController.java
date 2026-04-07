package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/** Responsible for the presentation of the ChatModel to the Client */
public class ChatViewController implements Initializable {

    @FXML private Button sendButton;

    @FXML private TextField inputField;

    @FXML private VBox chatVBox;

    @FXML private HBox controlBar;

    @FXML private ScrollPane scrollPane;

    @FXML private VBox Chat;

    private final ChatModel chatModel;

    private final String username;

    private final ChatController controller;

    private static final int CHAT_PADDING = 20;

    public ChatViewController() {
        this(null, null, null);
    }

    public ChatViewController(
            String username, ChatModel chatModel, ChatController controller) {
        this.username = username;
        this.controller = controller;
        this.chatModel = chatModel;
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        inputField.setOnAction(event -> sendMessage());
        sendButton.setOnAction(event -> sendMessage());
        scrollPane.vvalueProperty().bind(chatVBox.heightProperty());
    }

    public void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            inputField.clear();
            Message msg = new Message(chatModel.getChattype(), chatModel.lobbyId, username, chatModel.getTarget(), message);
            controller.onSendToNetwork(msg);
        }
    }

    public void showMessage() {
        String msg = chatModel.viewNextMessage();
        Label label = new Label(msg);
        label.getStyleClass().add("info-text");
        label.setWrapText(true);
        label.maxWidthProperty().bind(chatVBox.widthProperty().subtract(CHAT_PADDING));
        chatVBox.getChildren().add(label);
    }



}
