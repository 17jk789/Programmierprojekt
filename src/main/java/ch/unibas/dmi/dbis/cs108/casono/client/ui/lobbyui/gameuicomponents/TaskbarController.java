package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.gameuicomponents;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Controller für die interaktive Taskleiste innerhalb der Poker-UI.
 *
 * Verantwortlich für:
 * - Drag-and-Drop-Verschieben der Taskleiste,
 * - Eingabe und Verwaltung von Spieleinsätzen,
 * - Steuerung allgemeiner Menüfunktionen wie Exit.
 *
 * Dient als Schnittstelle zwischen Benutzerinteraktionen und der Spiellogik.
 */
public class TaskbarController {

    @FXML private HBox taskbar;
    @FXML private TextField taskbarInput;

    private double xOffset = 0;
    private double yOffset = 0;

    // Logik für das Verschieben der Taskleiste: Maus gedrückt
    @FXML
    private void onTaskbarPressed(MouseEvent event) {
        xOffset = event.getSceneX() - taskbar.getLayoutX();
        yOffset = event.getSceneY() - taskbar.getLayoutY();
    }

    // Logik für das Verschieben der Taskleiste: Maus ziehen
    @FXML
    private void onTaskbarDragged(MouseEvent event) {
        taskbar.setLayoutX(event.getSceneX() - xOffset);
        taskbar.setLayoutY(event.getSceneY() - yOffset);

        taskbar.setScaleX(0.9);
        taskbar.setScaleY(0.9);
    }

    // Logik für das Verschieben der Taskleiste: Maus loslassen
    @FXML
    private void onTaskbarReleased(MouseEvent event) {
        taskbar.setScaleX(1.0);
        taskbar.setScaleY(1.0);
    }

    // Enter-Taste im Textfeld (in der Taskleiste)
    @FXML
    private void onInputSubmitted(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processBet();
        }
    }

    // Submit-Button (in der Taskleiste)
    @FXML
    private void onInputSubmittedAction() {
        processBet();
    }

    // Exit-Button (in der Taskleiste)
    // TODO: Logik implementieren, um zur Lobby zurückzukehren,
    // ohne die gesamte Anwendung zu schließen (kein System.exit/Platform.exit).
    @FXML
    private void onExitButtonClick() {
        Platform.exit();
    }

    // Logik zum Verarbeiten des Einsatzes
    private void processBet() {
        String input = taskbarInput.getText();
        if (input != null && !input.isEmpty()) {
            // TODO: System.out durch echte Kommunikation mit der GameEngine ersetzen, sobald die Logik steht
            System.out.println("Einsatz gesetzt: " + input + " Credits");
            taskbarInput.clear();
        }
    }
}