package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the Casono Game UI.
 *
 * <p>Starts the JavaFX application, loads the graphical user interface from the FXML file, and
 * initializes the main stage for the game.
 *
 * <p>Tasks: - Loads the FXML interface "/ui-structure/Casinogameui.fxml". - Loads the
 * application icon from "/images/logoinverted.png". - Starts the application in full-screen mode.
 */
public class CasinoGameUI extends Application {

    /** default constructor */
    public CasinoGameUI() {
        // default no-arg constructor
    }

    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    /**
     * Starts the main stage of the application.
     *
     * @param stage The main stage provided by the system.
     * @throws IOException If the FXML file or resources cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(CasinoGameUI.class.getResource("/ui-structure/Casinogameui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setTitle("Casono (GAME)");

        String iconPath = getClass().getResource("/images/logoinverted.png").toExternalForm();
        stage.getIcons().add(new javafx.scene.image.Image(iconPath));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * Starting point of the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
