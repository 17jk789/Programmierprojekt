package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/**
 * Main UI application for Casono.
 * Loads the main FXML layout and sets up the stage.
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX Application class for the Casono main UI.
 * <p>
 * Standardkonstruktor für die Anwendung.
 */
public class Casinomainui extends Application {

    /**
     * Standardkonstruktor.
     */
    public Casinomainui() {
        // Standardkonstruktor
    }
    @Override
    /**
     * Starts the JavaFX application and loads the main UI.
     * @param stage The primary stage for this application.
     * @throws IOException If loading the FXML fails.
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui-structure/Casinomainui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Casono");
        
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("/images/logoinverted.png").toExternalForm()));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * Main entry point for launching the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
