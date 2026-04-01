package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/** Main UI application for Casono. Loads the main FXML layout and sets up the stage. */
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Application class for the Casono main UI.
 *
 * <p>
 * Default constructor for the application.
 */
public class Casinomainui extends Application {

    /** Default constructor. */
    public Casinomainui() {
        // Default constructor
    }

    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;

    @Override
    /**
     * Starts the JavaFX application and loads the main UI.
     *
     * @param stage The primary stage for this application.
     * @throws IOException If loading the FXML fails.
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui-structure/Casinomainui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Casono");
        javafx.scene.image.Image icon = new javafx.scene.image.Image(
                getClass().getResource("/images/logoinverted.png").toExternalForm());
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
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
