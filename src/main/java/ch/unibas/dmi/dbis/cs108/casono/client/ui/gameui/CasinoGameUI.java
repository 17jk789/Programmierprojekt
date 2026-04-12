package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import ch.unibas.dmi.dbis.cs108.casono.client.game.GameService;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.GameClient;
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

    private static String username;

    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    public CasinoGameUI() {
        // default no-arg constructor
    }

    public static void setClientService(ClientService clientService) {
        CasinoGameUI.clientService = clientService;
    }

    public static void setUsername(String username) {
        CasinoGameUI.username = username;
    }

    @Override
    public void start(Stage stage) throws IOException {

        if (clientService == null) {
            clientService = ch.unibas.dmi.dbis.cs108.casono.client.ClientApp.getSharedClientService();
        }
        if (clientService == null) {
            throw new IllegalStateException(
                    "CasinoGameUI: clientService is null. "
                            + "Call CasinoGameUI.setClientService(...) or start via ClientApp with a shared connection.");
        }

        String effectiveUsername = normalize(username);

        if (effectiveUsername == null) {
            effectiveUsername =
                    normalize(ch.unibas.dmi.dbis.cs108.casono.client.ClientApp.getSharedUsername());
        }

        if (effectiveUsername == null) {
            effectiveUsername = "Guest-" + UUID.randomUUID().toString().substring(0, 8);
        }

        LOG.info("CasinoGameUI starting: effectiveUsername='" + effectiveUsername
                + "', injectedUsername='" + username
                + "', sharedUsername='" + ch.unibas.dmi.dbis.cs108.casono.client.ClientApp.getSharedUsername()
                + "', hasClientService=" + (clientService != null));

        FXMLLoader fxmlLoader =
                new FXMLLoader(CasinoGameUI.class.getResource("/ui-structure/Casinogameui.fxml"));
        Parent root = fxmlLoader.load();
        CasinoGameController controller = fxmlLoader.getController();

        int gameId = 1; // TODO echte gameId einsetzen
        GameClient gameClient = new GameClient(clientService, gameId);
        GameService gameService = new GameService(gameClient);
        controller.setGameService(gameService);

        controller.setMyPlayerId(PlayerId.of(effectiveUsername));

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setTitle("Casono");

        String iconPath = getClass().getResource("/images/logoinverted.png").toExternalForm();
        stage.getIcons().add(new javafx.scene.image.Image(iconPath));

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

        controller.start();
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    public static void main(String[] args) {
        launch();
    }
}
