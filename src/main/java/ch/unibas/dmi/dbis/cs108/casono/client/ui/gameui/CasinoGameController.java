package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller für die Casino-Spielfläche.
 *
 * <p>
 * Verantwortlich für: - die Darstellung des Pokertisches und der
 * Spieleroberfläche, - die
 * Verarbeitung von Benutzereingaben, - die Schnittstelle zur GameEngine und zum
 * Netzwerkprotokoll.
 *
 * <p>
 * Hinweise: - Die Methode `onTableClick()` dient aktuell nur als Test-Logik.
 * Sie ist ggf. nicht
 * mehr funktionsfähig und wird zukünftig durch die finale Spielinteraktion
 * ersetzt.
 */
public class CasinoGameController {

    /** Standardkonstruktor. Wird von FXML verwendet. */
    public CasinoGameController() {
        // default constructor for FXML
    }

    @FXML
    private Label welcomeText;
    @FXML
    private VBox casinoTable;

    // TODO: Test-Logik: wird durch echte Spielinteraktionen ersetzt,
    // sobald die GameEngine fertig ist

    /**
     * Temporäre Test-Methode, die bei Klick auf den Tisch eine Platzhalteraktion
     * ausführt.
     *
     * <p>
     * Wird in der finalen Implementierung durch die Spiel-Logik ersetzt.
     */
    @FXML
    public void onTableClick() {
        welcomeText.setText("Einsatz akzeptiert!");
    }
}
