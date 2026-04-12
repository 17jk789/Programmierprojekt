package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.game.GameService;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private static final Logger LOGGER = LogManager.getLogger(CasinoBrowserController.class);

    @FXML private HBox taskbar;
    @FXML private TextField taskbarInput;
    @FXML private Button betButton;
    @FXML private Button callButton;
    @FXML private Button foldButton;
    @FXML private Button raiseButton;
    @FXML private Label moneyLabel;

    private GameService gameService;
    private PlayerId myPlayerId;
    private double xOffset = 0;
    private double yOffset = 0;
    private static final double TASKBAR_SCALE = 0.95;
    private static final int MIN_CREDITS = 5;
    private static final int MAX_CREDITS = 100000;
    private static final int CREDIT_STEP = 5;
    private static final String STYLE_YELLOW_BUTTON = "yellow-button";
    private static final String STYLE_GRAY_BUTTON = "gray-button";
    private static final String STYLE_RED_BUTTON = "red-button";
    private static final String STYLE_YELLOW_INPUT = "yellow-input-field";
    private static final String STYLE_GRAY_INPUT = "gray-input-field";
    private static final String STYLE_RED_INPUT = "red-input-field";
    private static final double SCALE_NORMAL = 1.0;

    /** Standard constructor. Used by FXML. */
    public TaskbarController() {
        // default constructor for FXML
    }

    /**
     * Updates the state of the basic action buttons (Bet, Call, Fold, Raise) and the input field
     *
     * @param isMyTurn Indicates whether it is currently the player's turn.
     * @param isOut Indicates whether the player is currently out of the game (folded or all-in).
     */
    private void updateBasicButtons(boolean isMyTurn, boolean isOut) {

        if (isOut) {
            setRedButton(betButton);
            setRedButton(callButton);
            setRedButton(foldButton);
            setRedButton(raiseButton);
            setRedInputField(taskbarInput);
            return;
        }

        if (isMyTurn) {
            activateButton(betButton);
            activateButton(callButton);
            activateButton(foldButton);
            activateButton(raiseButton);
            activateInputField(taskbarInput);
        } else {
            deactivateButton(betButton);
            deactivateButton(callButton);
            deactivateButton(foldButton);
            deactivateButton(raiseButton);
            deactivateInputField(taskbarInput);
        }
    }

    /**
     * Sets the given button to a disabled state with red styling, indicating that the player is out
     *
     * @param b The button to be styled as red and disabled
     */
    private void setRedButton(Button b) {
        b.setDisable(true);

        b.getStyleClass().remove(STYLE_YELLOW_BUTTON);
        b.getStyleClass().remove(STYLE_GRAY_BUTTON);

        if (!b.getStyleClass().contains(STYLE_RED_BUTTON)) {
            b.getStyleClass().add(STYLE_RED_BUTTON);
        }
    }

    /**
     * Sets the given input field to a disabled state with red styling, indicating that the player
     * is out
     *
     * @param t The text field to be styled as red and disabled
     */
    private void setRedInputField(TextField t) {
        t.setDisable(true);

        t.getStyleClass().remove(STYLE_YELLOW_INPUT);
        t.getStyleClass().remove(STYLE_GRAY_INPUT);

        if (!t.getStyleClass().contains(STYLE_RED_INPUT)) {
            t.getStyleClass().add(STYLE_RED_INPUT);
        }
    }

    /**
     * Activates the given button by enabling it and applying yellow styling, indicating that it is
     * the player's turn
     *
     * @param b The button to be activated and styled for the player's turn
     */
    private void activateButton(Button b) {
        b.setDisable(false);

        b.getStyleClass().remove(STYLE_GRAY_BUTTON);

        if (!b.getStyleClass().contains(STYLE_YELLOW_BUTTON)) {
            b.getStyleClass().add(STYLE_YELLOW_BUTTON);
        }
    }

    /**
     * Activates the given input field by enabling it and applying yellow styling, indicating that
     * it is the player's turn
     *
     * @param t The text field to be activated and styled for the player's turn
     */
    private void activateInputField(TextField t) {
        t.setDisable(false);

        t.getStyleClass().remove(STYLE_GRAY_INPUT);

        if (!t.getStyleClass().contains(STYLE_YELLOW_INPUT)) {
            t.getStyleClass().add(STYLE_YELLOW_INPUT);
        }
    }

    /**
     * Deactivates the given button by disabling it and applying gray styling, indicating that it is
     * not the player's turn
     *
     * @param b The button to be deactivated and styled for non-active state
     */
    private void deactivateButton(Button b) {
        b.setDisable(true);

        b.getStyleClass().remove(STYLE_YELLOW_BUTTON);
        b.getStyleClass().remove(STYLE_RED_BUTTON);

        if (!b.getStyleClass().contains(STYLE_GRAY_BUTTON)) {
            b.getStyleClass().add(STYLE_GRAY_BUTTON);
        }
    }

    /**
     * Deactivates the given input field by disabling it and applying gray styling, indicating that
     * it is not the player's turn
     *
     * @param t The text field to be deactivated and styled for non-active state
     */
    private void deactivateInputField(TextField t) {
        t.setDisable(true);

        t.getStyleClass().remove(STYLE_YELLOW_INPUT);
        t.getStyleClass().remove(STYLE_RED_INPUT);

        if (!t.getStyleClass().contains(STYLE_GRAY_INPUT)) {
            t.getStyleClass().add(STYLE_GRAY_INPUT);
        }
    }

    /**
     * Updates the displayed amount of money the player has. The amount is shown in the format "X$".
     *
     * @param amount The amount of money to display for the player.
     */
    public void setMoney(int amount) {
        moneyLabel.setText(amount + "$");
    }

    public void setGameService(GameService gameService, PlayerId myPlayerId) {
        this.gameService = gameService;
        this.myPlayerId = myPlayerId;
    }

    /**
     * Initializes the taskbar controller. This method is called automatically after the FXML
     * components have been loaded.
     */
    @FXML
    public void initialize() {
        updateBasicButtons(false, false);
    }

    /**
     * Updates the taskbar based on the current game state and the player's status. It checks if
     * it's the player's turn and whether they are out of the game (folded or all-in) to adjust the
     * button states and displayed money accordingly.
     *
     * @param state
     * @param myPlayerId
     */
    public void update(GameState state, PlayerId myPlayerId) {

        if (state == null || state.players == null || myPlayerId == null) {
            LOGGER.warn("Cannot update taskbar: invalid input");
            return;
        }

        Player me =
                state.players.stream()
                        .filter(p -> myPlayerId.equals(p.getId()))
                        .findFirst()
                        .orElse(null);

        if (me == null) {
            LOGGER.error("Player not found in GameState!");
            return;
        }

        int myIndex = state.players.indexOf(me);

        boolean isMyTurn = state.activePlayer == myIndex;
        boolean isOut = me.getState() == PlayerState.FOLDED;

        updateBasicButtons(isMyTurn, isOut);
        setMoney(me.getChips());
    }

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
        taskbar.setScaleX(SCALE_NORMAL);
        taskbar.setScaleY(SCALE_NORMAL);
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

    /** Called when the Call button is clicked. */
    @FXML
    private void onInputPlayerCall() {

        if (gameService == null) {
            LOGGER.error("GameService not initialized");
            return;
        }

        gameService.call();
        LOGGER.info("Player CALL");

        refreshGame();
    }

    /** Called when the Fold button is clicked. */
    @FXML
    private void onInputPlayerFold() {

        if (gameService == null) {
            LOGGER.error("GameService not initialized");
            return;
        }

        gameService.fold();
        LOGGER.info("Player FOLD");

        refreshGame();
    }

    /** Called when the Raise button is clicked. */
    @FXML
    private void onInputPlayerRaise() {

        String input = taskbarInput.getText();

        try {
            int amount = Integer.parseInt(input.trim());

            gameService.raise(amount);

            LOGGER.info("Player RAISE {}", amount);

            taskbarInput.clear();

            refreshGame();

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid raise amount");
        }
    }

    /**
     * Refreshes the game state by fetching the latest state from the GameService and updating the
     * taskbar accordingly.
     */
    private void refreshGame() {

        try {
            GameState newState = gameService.refresh();
            update(newState, myPlayerId);

        } catch (Exception e) {
            LOGGER.error("Failed to refresh game state: {}", e.getMessage());
        }
    }

    /**
     * Called when the Exit button is clicked. Closes the current game stage and opens the lobby UI.
     */
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
                        javafx.stage.Stage newStage = new javafx.stage.Stage();
                        javafx.fxml.FXMLLoader fxmlLoader =
                                new javafx.fxml.FXMLLoader(
                                        getClass().getResource("/ui-structure/Casinomainui.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();
                        javafx.scene.Scene scene =
                                new javafx.scene.Scene(
                                        root, Casinomainui.SCENE_WIDTH, Casinomainui.SCENE_HEIGHT);
                        newStage.setTitle("Casono");
                        javafx.scene.image.Image icon =
                                new javafx.scene.image.Image(
                                        getClass()
                                                .getResource("/images/logoinverted.png")
                                                .toExternalForm());
                        newStage.getIcons().add(icon);
                        newStage.setScene(scene);
                        newStage.setFullScreen(true);
                        newStage.show();
                    } catch (Exception e) {
                        LOGGER.error("Error: starting the lobby UI: {}", e.getMessage(), e);
                    }
                });
    }

    /**
     * Processes the stake entered in the text field. Only integer values between 5 and 100,000
     * credits are accepted, in multiples of 5 (in increments of 5). The stake is currently only
     * displayed on the console.
     */
    private void processBet() {

        if (gameService == null) {
            LOGGER.error("Error: GameService not initialized");
            return;
        }

        String input = taskbarInput.getText();

        try {

            int credits = Integer.parseInt(input.trim());

            if (credits >= MIN_CREDITS && credits <= MAX_CREDITS && credits % CREDIT_STEP == 0) {

                gameService.bet(credits);

                LOGGER.info("Bet placed: {}", credits);

                taskbarInput.clear();

                refreshGame();

            } else {
                LOGGER.error("Error: Bet must be between 5 and 100000 and multiple of 5");
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Error: Invalid number entered");
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
