package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller für die interaktive Taskleiste innerhalb der Poker-UI.
 *
 * <p>Verantwortlich für: - Drag-and-Drop-Verschieben der Taskleiste, - Eingabe und Verwaltung von
 * Spieleinsätzen, - Steuerung allgemeiner Menüfunktionen wie Exit.
 */
public class TaskbarController {

    private static final Logger LOGGER = LogManager.getLogger(CasinoBrowserController.class);

    @FXML private HBox taskbar;
    @FXML private TextField taskbarInput;

    private double xOffset = 0;
    private double yOffset = 0;
    private static final double TASKBAR_SCALE = 0.9;
    private static final int MIN_CREDITS = 5;
    private static final int MAX_CREDITS = 100000;
    private static final int CREDIT_STEP = 5;

    /**
     * Wird aufgerufen, wenn die Taskleiste mit der Maus gedrückt wird. Speichert die relative
     * Position, um später korrekt zu verschieben.
     *
     * @param event Das Mausereignis
     */
    @FXML
    private void onTaskbarPressed(MouseEvent event) {
        xOffset = event.getSceneX() - taskbar.getLayoutX();
        yOffset = event.getSceneY() - taskbar.getLayoutY();
    }

    /**
     * Wird aufgerufen, während die Taskleiste mit der Maus gezogen wird. Aktualisiert die Position
     * und skaliert die Taskleiste leicht zur visuellen Rückmeldung.
     *
     * <p>TODO: Es muss noch gefixt werden, dass die Taskleiste nicht aus dem Fenster verschwinden
     * kann.
     *
     * @param event Das Mausereignis
     */
    @FXML
    private void onTaskbarDragged(MouseEvent event) {
        taskbar.setLayoutX(event.getSceneX() - xOffset);
        taskbar.setLayoutY(event.getSceneY() - yOffset);

        taskbar.setScaleX(TASKBAR_SCALE);
        taskbar.setScaleY(TASKBAR_SCALE);
    }

    /**
     * Wird aufgerufen, wenn die Maus über der Taskleiste losgelassen wird. Setzt die Skalierung der
     * Taskleiste wieder auf Normalgröße.
     *
     * @param event Das Mausereignis
     */
    @FXML
    private void onTaskbarReleased(MouseEvent event) {
        taskbar.setScaleX(1.0);
        taskbar.setScaleY(1.0);
    }

    /**
     * Wird aufgerufen, wenn im Textfeld die Enter-Taste gedrückt wird.
     *
     * @param event Das Tastaturereignis
     */
    @FXML
    private void onInputSubmitted(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processBet();
        }
    }

    /**
     * Wird aufgerufen, wenn der Submit-Button in der Taskleiste gedrückt wird. Löst die
     * Verarbeitung des Einsatzes aus.
     */
    @FXML
    private void onInputSubmittedAction() {
        processBet();
    }

    /**
     * Wird aufgerufen, wenn der Exit-Button in der Taskleiste gedrückt wird.
     *
     * <p>TODO: Logik implementieren, um zur Lobby zurückzukehren, ohne die gesamte Anwendung zu
     * schließen (kein System.exit/Platform.exit).
     */
    @FXML
    private void onExitButtonClick() {
        javafx.application.Platform.runLater(
                () -> {
                    // Game-Stage schließen
                    javafx.stage.Stage currentStage =
                            (javafx.stage.Stage) taskbar.getScene().getWindow();
                    currentStage.close();
                    // Lobby-UI starten
                    try {
                        new Casinomainui().start(new javafx.stage.Stage());
                    } catch (Exception e) {
                        LOGGER.error("Fehler beim Starten der Lobby-UI: {}", e.getMessage());
                    }
                });
    }

    /**
     * Verarbeitet den im Textfeld eingegebenen Einsatz. Es werden ausschließlich ganzzahlige Werte
     * im Bereich von 5 bis 100.000 Credits akzeptiert, die einem Vielfachen von 5 entsprechen
     * (5er-Schritte). Der Einsatz wird aktuell nur auf der Konsole ausgegeben.
     */
    private void processBet() {
        String input = taskbarInput.getText();
        try {
            int credits = Integer.parseInt(input.trim());

            if (credits >= MIN_CREDITS && credits <= MAX_CREDITS && credits % CREDIT_STEP == 0) {
                // TODO: Credits müssen an die GameEngine gesendet werden
                LOGGER.info("Einsatz gesetzt: {} Casono Credits", credits);
                taskbarInput.clear();
            } else {
                LOGGER.info("Fehler: Nur 5er-Schritte (5, 10, ... 100.000) erlaubt!");
            }
        } catch (NumberFormatException e) {
            LOGGER.info("Fehler: Bitte nur eine Zahl eingeben!");
        }
    }

    /**
     * Öffnet den integrierten Casono Webbrowser.
     *
     * <p>TODO: Ersetze die Start-URL durch die offizielle Projekt-Website (z.B. Tipps & Tricks
     * Seite), sobald die Inhalte für Strategien und Support bereitstehen.
     */
    @FXML
    private void onBrowserButtonClick() {
        CasinoBrowserController.open("wikipedia.org");
    }
}
