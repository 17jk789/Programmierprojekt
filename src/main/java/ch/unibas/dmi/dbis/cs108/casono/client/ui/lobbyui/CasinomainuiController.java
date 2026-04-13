package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Controller for the Casono main UI lobby. Handles UI initialization and user actions. */
public class CasinomainuiController {
    private static final Logger LOGGER = LogManager.getLogger(CasinomainuiController.class);

    @FXML private AnchorPane rootPane;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private ImageView logoView;
    @FXML private Rectangle greenBox;
    @FXML private Button exitbutton;
    @FXML private VBox casinoTable;
    @FXML private TextField usernameField;
    @FXML private Button loginButton;
    @FXML private AnchorPane chatContainer;

    private LobbyButtonTranslationManager translationManager;
    private LobbyButtonGridManager gridManager;
    private int nextButtonId = 1;
    private LobbyClient lobbyClient;
    private ChatController chatController;

    /** Default constructor used by FXMLLoader. */
    public CasinomainuiController() {
        // Default constructor
    }

    /**
     * Initializes the UI components and sets default values. If a shared {@link
     * ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService} exists (created at application
     * start), the controller reuses it so the connection remains open and already-logged-in.
     */
    @FXML
    public void initialize() {
        titleLabel.setText("Casono");
        subtitleLabel.setText("Texas Hold'em Poker");
        logoView.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));

        translationManager = LobbyButtonTranslationManager.getInstance();
        String host = System.getProperty("casono.server.host");
        int port = Integer.parseInt(System.getProperty("casono.server.port"));
        ClientService clientService = ClientApp.getSharedClientService();
        if (clientService == null) {
            try {
                clientService = new ClientService(host, port);
            } catch (RuntimeException e) {
                LOGGER.warn(
                        "Could not connect to server {}:{} — starting in offline mode: {}",
                        host,
                        port,
                        e.getMessage());
                clientService = new ClientService(true); // offline mode
            }
        }
        gridManager =
                new LobbyButtonGridManager(
                        new javafx.scene.layout.GridPane(), translationManager, clientService);
        // LobbyClient will use the provided ClientService; in offline mode calls will
        // fail with RuntimeException
        lobbyClient = new LobbyClient(clientService);
        // Fetch existing lobbies from server on startup so newly-created lobbies
        // by other clients are immediately visible.
        try {
            if (!lobbyClient.getClientService().isOffline()) {
                var lobbies = lobbyClient.getLobbyList();
                int bid = nextButtonId;
                for (var li : lobbies) {
                    try {
                        translationManager.addLobbyButton(bid++, li.id);
                    } catch (Exception e) {
                        LOGGER.warn("Could not add lobby button: {}", e.getMessage());
                    }
                }
                nextButtonId = bid;
            }
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to fetch lobby list at startup: {}", e.getMessage());
        }
        casinoTable.getChildren().clear();
        casinoTable.getChildren().add(gridManager.getGridPane());
        gridManager.renderLobbyButtons();

        initializeChat(clientService);
    }

    /**
     * Initializes the chat UI if a valid ClientService is available. If the client is offline or
     * the
     *
     * @param clientService
     */
    private void initializeChat(ClientService clientService) {
        if (clientService == null || clientService.isOffline() || chatContainer == null) {
            return;
        }
        try {
            String username = resolveChatUsername();
            chatController = new ChatController(username, clientService);
            URL resource = getClass().getResource("/ui-structure/components/chatui/chatbox.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(chatController.getChatBoxController());
            Node chatNode = loader.load();
            chatContainer.getChildren().setAll(chatNode);
            AnchorPane.setTopAnchor(chatNode, 0.0);
            AnchorPane.setBottomAnchor(chatNode, 0.0);
            AnchorPane.setLeftAnchor(chatNode, 0.0);
            AnchorPane.setRightAnchor(chatNode, 0.0);
        } catch (IOException e) {
            LOGGER.warn("Could not initialize lobby chat UI: {}", e.getMessage());
        }
    }

    /**
     * Resolves the username to be used in the chat. It first checks for a shared username set at
     * the
     *
     * @return The resolved username, or "Guest" if no valid username is found.
     */
    private String resolveChatUsername() {
        String shared = ClientApp.getSharedUsername();
        if (shared != null && !shared.isBlank()) {
            return shared.trim();
        }
        if (usernameField != null
                && usernameField.getText() != null
                && !usernameField.getText().isBlank()) {
            return usernameField.getText().trim();
        }
        return "Guest";
    }

    /** Handles the login button action. Validates input and calls LobbyClient.login(). */
    @FXML
    public void handleLoginButton() {
        String username = usernameField.getText();
        if (username == null || username.isBlank()) {
            showAlert("Please enter a username.");
            return;
        }
        // Only allow alphanumeric, _ and -
        if (!username.matches("[a-zA-Z0-9_-]+")) {
            showAlert("Only letters, numbers, '_' and '-' are allowed!");
            return;
        }
        if (lobbyClient.getClientService().isOffline()) {
            showAlert("Offline mode: cannot send login to server.");
            return;
        }
        try {
            lobbyClient.login(username);
            showAlert("Login sent: " + username);
        } catch (RuntimeException e) {
            LOGGER.error("Login failed: {}", e.getMessage());
            showAlert("Login failed: " + e.getMessage());
        }
    }

    /** Shows an alert dialog with the given message. */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        casinoTable.getChildren().clear();
        casinoTable.getChildren().add(gridManager.getGridPane());
        gridManager.renderLobbyButtons();
    }

    /** Handles the exit button action to close the application. */
    @FXML
    public void handleexitbutton() {
        if (chatController != null) {
            chatController.shutdown();
        }
        Platform.exit();
    }

    /**
     * Handles creation of a new lobby button. Attempts to create a lobby on the server via the
     * {@link LobbyButtonGridManager} and registers the new button in the local translation manager.
     * Errors are logged and displayed as an informational alert.
     */
    @FXML
    public void handleCreateLobbyButton() {
        if (translationManager.isFull()) {
            LOGGER.warn("Grid is full! No more lobbies available.");
            return;
        }
        int buttonId = nextButtonId++;
        try {
            String username = usernameField != null ? usernameField.getText() : "<unknown>";
            LOGGER.info("Creating lobby for user: {}", username);
            // avoid attempting to create a lobby when offline
            if (lobbyClient.getClientService().isOffline()) {
                LOGGER.warn("Cannot create lobby while offline");
                showAlert("Offline mode: cannot create lobby.");
                return;
            }
            int lobbyId = gridManager.createLobby();
            translationManager.addLobbyButton(buttonId, lobbyId);
            LOGGER.info("ButtonID: {}, LobbyID: {}", buttonId, lobbyId);
            gridManager.renderLobbyButtons();
        } catch (Exception e) {
            LOGGER.error("Failed to create or add lobby: {}", e.getMessage());
        }
    }
}
