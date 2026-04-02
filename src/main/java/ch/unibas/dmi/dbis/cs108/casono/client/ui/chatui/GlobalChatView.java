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

public class GlobalChatView {

    private final String username;
    private ChatModel globalChatModel;

    private final ChatController controller;

    @FXML private Button globalChatSendButton;

    @FXML private TextField globalChatInputField;

    @FXML private VBox globalChatVBox;

    @FXML private VBox globalChat;

    @FXML private ScrollPane globalChatScrollPane;

    private static final int CHAT_PADDING = 20;

    public GlobalChatView(String username, ChatModel globalChatModel, ChatController controller) {
        this.username = username;
        this.controller = controller;
        this.globalChatModel = globalChatModel;
    }

    @FXML
    public void initialize() {
        globalChatInputField.setOnAction(event -> sendGlobalMessage());
        globalChatSendButton.setOnAction(event -> sendGlobalMessage());
        globalChatScrollPane.vvalueProperty().bind(globalChatVBox.heightProperty());
    }

    public void sendGlobalMessage() {
        String message = globalChatInputField.getText().trim();
        if (!message.isEmpty()) {
            globalChatInputField.clear();
            Message msg = new Message(ChatType.GLOBAL, 0, username, null, message);
            controller.sendMessageToNetwork(msg);
        }
    }

    public void showGlobalMessage() {
        for (int i = 0; i < globalChatModel.count; i++) {
            String msg = globalChatModel.viewNextMessage();
            Label label = new Label(msg);
            label.getStyleClass().add("info-text");
            label.setWrapText(true);
            label.maxWidthProperty().bind(globalChatVBox.widthProperty().subtract(CHAT_PADDING));
            globalChatVBox.getChildren().add(label);
        }
    }

    public void setVisibility(Boolean bool) {
        globalChatVBox.setVisible(bool);
    }
}
