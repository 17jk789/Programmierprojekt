package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameService;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the Casono Game UI.
 *
 * <p>Starts the JavaFX application, loads the graphical user interface from the FXML file, and
 * initializes the main stage for the game.
 */
public class CasinoGameUI extends Application {

    private static final Logger LOG = Logger.getLogger(CasinoGameUI.class.getName());

    private static ClientService clientService;
    private static ChatController chatController;

    private static String username;
    private static int lobbyId = -1;

    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int GUEST_ID_LENGTH = 8;

    /** Default constructor for the CasinoGameUI application. */
    public CasinoGameUI() {
        // default no-arg constructor
    }

    /**
     * Sets the ClientService instance to be used by the application.
     *
     * @param clientService the ClientService instance to set
     */
    public static void setClientService(ClientService clientService) {
        CasinoGameUI.clientService = clientService;
    }

    /**
     * Sets the username to be used by the application.
     *
     * @param username the username to set
     */
    public static void setUsername(String username) {
        CasinoGameUI.username = username;
    }

    public static void setChatController(ChatController chatController) {
        CasinoGameUI.chatController = chatController;
    }

    /**
     * Sets the lobby ID to be used by the application.
     *
     * @param lobbyId the lobby ID to set
     */
    public static void setLobbyId(int lobbyId) {
        CasinoGameUI.lobbyId = lobbyId;
    }

    /**
     * The main entry point for the JavaFX application. This method is called after the application
     * is
     *
     * @param stage the primary stage for this application, onto which the application scene can be
     *     set. Applications may create other stages, if needed, but they will not be primary
     *     stages.
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {

        if (clientService == null) {
            clientService = ClientApp.getSharedClientService();
        }
        if (clientService == null) {
            throw new IllegalStateException(
                    "CasinoGameUI: clientService is null. "
                            + "Call CasinoGameUI.setClientService(...)"
                            + " or start via ClientApp with a shared connection.");
        }

        String effectiveUsername = normalize(username);

        if (effectiveUsername == null) {
            effectiveUsername = normalize(ClientApp.getSharedUsername());
        }

        if (effectiveUsername == null) {
            effectiveUsername =
                    "Guest-" + UUID.randomUUID().toString().substring(0, GUEST_ID_LENGTH);
        }

        LOG.info(
                "CasinoGameUI starting: effectiveUsername='"
                        + effectiveUsername
                        + "', injectedUsername='"
                        + username
                        + "', sharedUsername='"
                        + ClientApp.getSharedUsername()
                        + "', hasClientService="
                        + (clientService != null));

        FXMLLoader fxmlLoader =
                new FXMLLoader(CasinoGameUI.class.getResource("/ui-structure/Casinogameui.fxml"));
        Parent root = fxmlLoader.load();
        CasinoGameController controller = fxmlLoader.getController();

        if (lobbyId <= 0) {
            throw new IllegalStateException("CasinoGameUI: lobbyId must be set before start()");
        }

        GameClient gameClient = new GameClient(clientService, lobbyId);
        GameService gameService = new GameService(gameClient);
        controller.setGameService(gameService);

        controller.setMyPlayerId(PlayerId.of(effectiveUsername));
        controller.setChatContext(effectiveUsername, clientService, lobbyId);

        try {
            LobbyClient lobbyClient = new LobbyClient(clientService);
            controller.setLobbyPlayerNames(lobbyClient.fetchLobbyPlayerNames(lobbyId));
        } catch (Exception e) {
            LOG.fine("Could not preload lobby player names: " + e.getMessage());
        }

        controller.startChat(chatController);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setTitle("Casono");

        String iconPath = getClass().getResource("/images/logoinverted.png").toExternalForm();
        stage.getIcons().add(new javafx.scene.image.Image(iconPath));

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setOnHidden(e -> controller.stop());
        stage.show();

        controller.start();
    }

    /**
     * Normalizes a string by trimming whitespace and converting blank strings to null.
     *
     * @param s the string to normalize
     * @return the normalized string, or null if the input is null or blank
     */
    private static String normalize(String s) {
        if (s == null) {
            return null;
        }

        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    /**
     * The main method serves as the entry point for the application. It launches the JavaFX
     * application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }
}
