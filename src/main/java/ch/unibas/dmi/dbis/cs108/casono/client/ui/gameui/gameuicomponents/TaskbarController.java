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
 * Controller for the interactive taskbar within the poker UI.
 *
 * <p>Responsible for: - Drag-and-drop movement of the taskbar, - Input and management of game
 * stakes, - Control of general menu functions such as Exit.
 */
public class TaskbarController {

    /** Standard constructor. Used by FXML. */
    public TaskbarController() {
        // default constructor for FXML
    }

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
     * Called when the taskbar is clicked with the mouse. Saves the relative position for later,
     * correct repositioning.
     *
     * @param event The mouse event
     */
    @FXML
    private void onTaskbarPressed(MouseEvent event) {
        xOffset = event.getSceneX() - taskbar.getLayoutX();
        yOffset = event.getSceneY() - taskbar.getLayoutY();
    }

    /**
     * Called while dragging the taskbar with the mouse. Updates the position and slightly scales
     * the taskbar for visual feedback.
     *
     * <p>TODO: It still needs to be fixed that the taskbar cannot disappear out of the window.
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
     * Called when the mouse cursor is released over the taskbar. Resets the taskbar scaling to
     * normal size.
     *
     * @param event The mouse event
     */
    @FXML
    private void onTaskbarReleased(MouseEvent event) {
        taskbar.setScaleX(1.0);
        taskbar.setScaleY(1.0);
    }

    /**
     * Called up when the Enter key is pressed in the text field.
     *
     * @param event The keyboard event
     */
    @FXML
    private void onInputSubmitted(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processBet();
        }
    }

    /**
     * Called when the submit button in the taskbar is pressed. Triggers the processing of the
     * deployment.
     */
    @FXML
    private void onInputSubmittedAction() {
        processBet();
    }

    @FXML
    private void onExitButtonClick() {
        javafx.application.Platform.runLater(
                () -> {
                    // Close game stage
                    javafx.stage.Stage currentStage =
                            (javafx.stage.Stage) taskbar.getScene().getWindow();
                    currentStage.close();
                    // Start lobby UI
                    try {
                        new Casinomainui().start(new javafx.stage.Stage());
                    } catch (Exception e) {
                        LOGGER.error("Fehler beim Starten der Lobby-UI: {}", e.getMessage());
                    }
                });
    }

    /**
     * Processes the stake entered in the text field. Only integer values between 5 and 100,000
     * credits are accepted, in multiples of 5 (in increments of 5). The stake is currently only
     * displayed on the console.
     */
    private void processBet() {
        String input = taskbarInput.getText();
        try {
            int credits = Integer.parseInt(input.trim());

            if (credits >= MIN_CREDITS && credits <= MAX_CREDITS && credits % CREDIT_STEP == 0) {
                // TODO: Credits must be sent to the GameEngine
                LOGGER.info("Bet set: {} Casono Credits", credits);
                taskbarInput.clear();
            } else {
                LOGGER.error("Error: Only increments of 5 (5, 10, ... 100,000) are allowed!");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error: Please enter only one number!");
        }
    }

    /**
     * Opens the integrated Casono web browser.
     *
     * <p>TODO: Replace the start URL with the official project website (e.g., Tips & Tricks page)
     * once the content for strategies and support is available.
     */
    @FXML
    private void onBrowserButtonClick() {
        CasinoBrowserController.open("wikipedia.org");
    }
}
