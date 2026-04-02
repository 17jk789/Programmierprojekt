package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatModel;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatType;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/** Responsible for the presentation of the ChatModel to the Client */
public class ChatViewController {

    private final GlobalChatView globalChatView;

    private LobbyChatView lobbyChatView;

    private final String username;

    private final ChatController controller;

    private final Timer timer;

    private Boolean lobbyActivated;

    @FXML private VBox whisperChatVBox;

    @FXML private VBox whisperChat;

    @FXML private ToggleButton changeGlobalChatButton;

    @FXML private ToggleButton changeLobbyChatButton;

    @FXML private ToggleButton changeWhisperChatButton;

    @FXML private ButtonBar buttonBar;

    private static final int REFRESH_TIME = 1000;

    private static final int CHAT_PADDING = 20;

    public ChatViewController() {
        this(null, null, null);
    }

    public ChatViewController(
            String username, ChatModel globalChatModel, ChatController controller) {
        this.username = username;
        this.controller = controller;
        this.globalChatView =
                new GlobalChatView(username, new ChatModel(ChatType.GLOBAL, username), controller);
        this.timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (controller.receiveMessage()) {
                            globalChatView.showGlobalMessage();
                            if (lobbyActivated) {
                                lobbyChatView.showLobbyMessage();
                            }
                        }
                    }
                },
                0,
                REFRESH_TIME);
    }

    @FXML
    public void initialize() {}

    @FXML
    public void setLobbyChat(int lobbyId) {
        this.lobbyChatView =
                new LobbyChatView(
                        username, new ChatModel(ChatType.LOBBY, username), controller, lobbyId);

        changeGlobalChatButton.setOnAction(event -> switchChat("globalChatButton"));
        changeLobbyChatButton.setOnAction(event -> switchChat("lobbyChatButton"));
        changeWhisperChatButton.setOnAction(event -> switchChat("whisperChatButton"));

        ToggleGroup toggleGroup = new ToggleGroup();
        changeGlobalChatButton.setToggleGroup(toggleGroup);
        changeLobbyChatButton.setToggleGroup(toggleGroup);
        changeWhisperChatButton.setToggleGroup(toggleGroup);
        this.lobbyActivated = true;
    }

    @FXML
    public void switchChat(String button) {
        switch (button) {
            case "globalChatButton":
                globalChatView.setVisibility(true);
                lobbyChatView.setVisibility(false);

            case "lobbyChatButton":
                globalChatView.setVisibility(false);
                lobbyChatView.setVisibility(true);

            case "whisperChatButton":
                globalChatView.setVisibility(false);
                lobbyChatView.setVisibility(false);
        }
    }
}
