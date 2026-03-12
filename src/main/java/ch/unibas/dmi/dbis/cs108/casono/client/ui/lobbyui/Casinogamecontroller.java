package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

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
 * - Die Methode `onTableClick()` dient aktuell als Test-Logik und wird später
 *   durch echte Spielinteraktionen ersetzt.
 * - Die Browserfunktion (`onBrowserButtonClick()`) ist geplant, um ein
 *   integriertes Hilfetool für Regeln oder Support bereitzustellen.
 */
public class Casinogamecontroller {

    @FXML private Label welcomeText;
    @FXML private VBox casinoTable;

    // TODO: Test-Logik: wird durch echte Spielinteraktionen ersetzt, sobald die GameEngine fertig ist
    @FXML
    public void onTableClick() {
        welcomeText.setText("Einsatz akzeptiert!");
    }

    // TODO: Implementierung des Casono-Browsers.
    // Dient als integriertes Hilfe-Tool bei technischen Problemen oder Regelfragen
    // und wird vervollständigt, sobald die Support-Inhalte bereitstehen.
    // @FXML
    // private void onBrowserButtonClick() {
    // }
}