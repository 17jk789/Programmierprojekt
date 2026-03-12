package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Hauptklasse für das Casino-Spiel-UI.
 *
 * Startet die JavaFX-Anwendung, lädt die grafische Oberfläche aus der FXML-Datei
 * und initialisiert die Haupt-Stage für das Spiel.
 *
 * Aufgaben:
 * - Lädt die FXML-Oberfläche "/ui-structure/Casinogameui.fxml".
 * - Lädt das Anwendungs-Icon aus "/images/logoinverted.png".
 * - Startet die Anwendung im Vollbildmodus.
 */
public class Casinogameui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Casinogameui.class.getResource("/ui-structure/Casinogameui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Casono (GAME)");

        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("/images/logoinverted.png").toExternalForm()));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
