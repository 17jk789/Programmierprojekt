package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Responsible for the presentation of the ChatModel to the Client
 */

public class ChatViewController {

    private final ChatModel chatmodel;
    private final String username;
    private final ChatController controller;
    private final Timer timer;

    @FXML
    private VBox chatVBox;

    @FXML
    private TextField inputField;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private Button sendButton;

    @FXML
    private Button refreshButton;

    private static final int CHAT_PADDING = 20;

    public ChatViewController() {
        this.username = null;
        this.chatmodel = null;
        this.controller = null;
        this.timer = new Timer();
    }

    public ChatViewController(String username, ChatModel chatmodel, ChatController controller) {
        this.username = username;
        this.chatmodel = chatmodel;
        this.controller = controller;
        this.timer = new Timer();
        if (this.controller != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.err.println("tick");
                    if (ChatViewController.this.controller.receiveMessage()) {
                        showMessage();
                    }
                }
            }, 0, 1000);
        }
    }

    @FXML
    public void initialize() {
        if (inputField != null) {
            inputField.setOnAction(event -> sendMessage());
        }
        if (sendButton != null) {
            sendButton.setOnAction(event -> sendMessage());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(event -> showMessage());
        }
        if (chatScrollPane != null && chatVBox != null) {
            chatScrollPane.vvalueProperty().bind(chatVBox.heightProperty());
        }
    }

    public void showMessage() {
        while (chatmodel.count > 0) {
            String msg = chatmodel.viewNextMessage();
            Label label = new Label(msg);
            label.getStyleClass().add("info-text");
            label.setWrapText(true);
            label.maxWidthProperty().bind(chatVBox.widthProperty().subtract(CHAT_PADDING));
            chatVBox.getChildren().add(label);
        }

    }

    public void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            inputField.clear();
            this.controller.sendMessage(message, username);
        }
    }

    public void endController() {
        timer.cancel();
    }
}
