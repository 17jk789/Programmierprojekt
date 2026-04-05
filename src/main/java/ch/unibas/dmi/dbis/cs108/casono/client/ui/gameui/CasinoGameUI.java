package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hauptklasse für das Casino-Spiel-UI.
 *
 * <p>Startet die JavaFX-Anwendung, lädt die grafische Oberfläche aus der FXML-Datei und
 * initialisiert die Haupt-Stage für das Spiel.
 *
 * <p>Aufgaben: - Lädt die FXML-Oberfläche "/ui-structure/Casinogameui.fxml". - Lädt das
 * Anwendungs-Icon aus "/images/logoinverted.png". - Startet die Anwendung im Vollbildmodus.
 */
public class CasinoGameUI extends Application {

    /** Standardkonstruktor. */
    public CasinoGameUI() {
        // default no-arg constructor
    }

    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    /**
     * Startet die Haupt-Stage der Anwendung.
     *
     * @param stage Die vom System bereitgestellte Haupt-Stage.
     * @throws IOException Wenn die FXML-Datei oder Ressourcen nicht geladen werden können.
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
     * Startpunkt der Anwendung.
     *
     * @param args Befehlszeilenargumente.
     */
    public static void main(String[] args) {
        launch();
    }
}
