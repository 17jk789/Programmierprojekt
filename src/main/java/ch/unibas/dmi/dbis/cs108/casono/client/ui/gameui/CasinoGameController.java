package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

// import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.input.KeyCode;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.input.MouseEvent;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
// import javafx.stage.Stage;
// import javafx.scene.Scene;
// import javafx.scene.web.WebView;

/**
 * Controller für die Casino-Spielfläche.
 *
 * Verantwortlich für:
 * - die Darstellung des Pokertisches und der Spieleroberfläche,
 * - die Verarbeitung von Benutzereingaben,
 * - die Schnittstelle zur GameEngine und zum Netzwerkprotokoll.
 *
 * Hinweise:
 * - Die Methode `onTableClick()` dient aktuell nur als Test-Logik.
 *   Sie ist ggf. nicht mehr funktionsfähig und wird zukünftig
 *   durch die finale Spielinteraktion ersetzt.
 */
public class CasinoGameController {

    @FXML private Label welcomeText;
    @FXML private VBox casinoTable;

    // TODO: Test-Logik: wird durch echte Spielinteraktionen ersetzt, sobald die GameEngine fertig ist
    @FXML
    public void onTableClick() {
        welcomeText.setText("Einsatz akzeptiert!");
    }
}