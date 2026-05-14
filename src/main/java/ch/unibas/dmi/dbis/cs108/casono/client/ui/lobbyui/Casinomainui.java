package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/** Main UI application for Casono. Loads the main FXML layout and sets up the stage. */
import ch.unibas.dmi.dbis.cs108.casono.ui.sound.SoundManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * JavaFX Application class for the Casono main UI.
 *
 * <p>Default constructor for the application.
 */
public class Casinomainui extends Application {

    /** Default constructor. */
    public Casinomainui() {
        // Default constructor
    }

    public static final int SCENE_WIDTH = 1200;
    public static final int SCENE_HEIGHT = 800;

    @Override
    /**
     * Starts the JavaFX application and loads the main UI.
     *
     * @param stage The primary stage for this application.
     * @throws IOException If loading the FXML fails.
     */
    public void start(Stage stage) throws IOException {
        // Pre-load sounds to avoid delays on first play
        SoundManager.getInstance().preloadSounds();

        // If the launcher passed an address argument (ip:port), expose it as
        // system properties so controllers can read it without embedding defaults.
        var params = getParameters();
        processServerParameters(params);

        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/ui-structure/Casinomainui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Casono");
        javafx.scene.image.Image icon =
                new javafx.scene.image.Image(
                        getClass().getResource("/images/logoinverted.png").toExternalForm());
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        // Add F11 fullscreen toggle
        scene.setOnKeyPressed(
                event -> {
                    if (event.getCode() == KeyCode.F11) {
                        stage.setFullScreen(!stage.isFullScreen());
                        event.consume();
                    }
                });

        stage.show();
    }

    private void processServerParameters(Application.Parameters params) {
        if (params == null) {
            return;
        }

        var raw = params.getRaw();
        if (raw == null || raw.isEmpty()) {
            return;
        }

        String arg = raw.get(0);
        String[] parts = arg.split(":", 2);
        if (parts.length == 2) {
            System.setProperty("casono.server.host", parts[0]);
            System.setProperty("casono.server.port", parts[1]);
        }
    }

    /**
     * Main entry point for launching the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
