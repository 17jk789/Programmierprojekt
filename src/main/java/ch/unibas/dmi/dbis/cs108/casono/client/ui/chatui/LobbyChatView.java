package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LobbyChatView {

    private final String username;
    private final int lobbyId;
    private ChatModel lobbyChatModel;

    private final ChatController controller;

    @FXML private Button lobbyChatSendButton;

    @FXML private TextField lobbyChatInputField;

    @FXML private VBox lobbyChatVBox;

    @FXML private VBox lobbyChat;

    @FXML private ScrollPane lobbyChatScrollPane;

    private static final int CHAT_PADDING = 20;

    public LobbyChatView(
            String username, ChatModel lobbyChatModel, ChatController controller, int lobbyId) {
        this.username = username;
        this.controller = controller;
        this.lobbyChatModel = lobbyChatModel;
        this.lobbyId = lobbyId;
    }

    @FXML
    public void initialize() {
        lobbyChatInputField.setOnAction(event -> sendLobbyMessage());
        lobbyChatSendButton.setOnAction(event -> sendLobbyMessage());
        lobbyChatScrollPane.vvalueProperty().bind(lobbyChatVBox.heightProperty());
    }

    public void sendLobbyMessage() {
        String message = lobbyChatInputField.getText().trim();
        if (!message.isEmpty()) {
            lobbyChatInputField.clear();
            Message msg = new Message(ChatType.LOBBY, lobbyId, username, null, message);
            controller.sendMessageToNetwork(msg);
        }
    }

    public void showLobbyMessage() {
        for (int i = 0; i < lobbyChatModel.count; i++) {
            String msg = lobbyChatModel.viewNextMessage();
            Label label = new Label(msg);
            label.getStyleClass().add("info-text");
            label.setWrapText(true);
            label.maxWidthProperty().bind(lobbyChatVBox.widthProperty().subtract(CHAT_PADDING));
            lobbyChatVBox.getChildren().add(label);
        }
    }

    public void setVisibility(Boolean bool) {
        lobbyChatVBox.setVisible(bool);
    }
}
