package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Responsible for the presentation of the ChatModel to the Client */
public class ChatViewController implements Initializable {

    private final ChatBoxController chatBoxController;
    @FXML private Button sendButton;

    @FXML private TextField inputField;

    @FXML private ScrollPane scrollPane;

    @FXML private VBox chat;

    public Tab getChatTab() {
        return chatTab;
    }

    public void setChatTab(Tab chatTab) {
        this.chatTab = chatTab;
    }

    @FXML private Tab chatTab;

    public Boolean tabIsOpen;

    private final ChatModel chatModel;

    private final ChatController controller;

    private static final int CHAT_PADDING = 20;

    public ChatViewController(
            ChatController chatController,
            ChatModel chatModel,
            ChatBoxController chatBoxController) {
        this.controller = chatController;
        this.chatModel = chatModel;
        this.chatBoxController = chatBoxController;
        tabIsOpen = true;
    }

    /**
     * Initializes the controller after the FXML root element has been processed. Sets up event
     * handlers for sending messages via the input field or button and ensures the ScrollPane
     * automatically scrolls to the bottom when new messages are added.
     *
     * @param location The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        inputField.setOnAction(_ -> sendMessage());
        sendButton.setOnAction(_ -> sendMessage());
        scrollPane.vvalueProperty().bind(chat.heightProperty());
    }

    /**
     * Retrieves the text from the input field, creates a new {@link Message} object using the
     * current model state, and passes it to the {@link ChatController} for network transmission.
     * The input field is cleared after sending.
     */
    public void sendMessage() {
        String message = inputField.getText().trim();

        message = message.replace("'", " ");

        if (!message.isEmpty()) {
            inputField.clear();
            String currentUsername = controller.getCurrentUsername();
            Message msg =
                    new Message(
                            chatModel.getChattype(),
                            chatModel.lobbyId,
                            currentUsername,
                            chatModel.getTarget(),
                            message);
            controller.onSendToNetwork(msg);
        }
    }

    /**
     * Displays a message in the chat interface. This method creates a new Label for the message
     * text and adds it to the message container. It uses {@link Platform#runLater(Runnable)} to
     * ensure the UI update happens on the JavaFX Application Thread.
     *
     * @param msg The {@link Message} object containing the content and metadata to display.
     */
    public void showMessage(Message msg) {
        if (!tabIsOpen) {
            tabIsOpen = true;
            chatBoxController.reopenChatTab(chatTab);
        }
        Platform.runLater(
                () -> {
                    String msgText =
                            String.format(
                                    "[%s] %s: %s", msg.timestamp, msg.sender, msg.getMessage());
                    Label label = new Label(msgText);
                    label.getStyleClass().add("info-text");
                    label.setWrapText(true);
                    label.maxWidthProperty().bind(chat.widthProperty().subtract(CHAT_PADDING));
                    chat.getChildren().add(label);
                });
    }
}
