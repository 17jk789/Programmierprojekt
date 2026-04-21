package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameService;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private static final Logger LOGGER = LogManager.getLogger(TaskbarController.class);

    @FXML private HBox taskbar;
    @FXML private TextField taskbarInput;
    @FXML private Button betButton;
    @FXML private Button callButton;
    @FXML private Button foldButton;
    @FXML private Button raiseButton;
    @FXML private Label moneyLabel;

    private GameService gameService;
    private PlayerId myPlayerId;
    private GameState lastState;
    private double xOffset = 0;
    private double yOffset = 0;
    private static final double TASKBAR_SCALE = 0.95;
    private static final double PREFLOP_BLOCK_RATIO = 0.50;
    private static final double FLOP_WARN_RATIO = 0.50;
    private static final double FLOP_BLOCK_RATIO = 0.80;
    private static final double TURN_RIVER_WARN_RATIO = 0.80;
    private static final double TURN_RIVER_BLOCK_RATIO = 1.00;
    private static final int SMALL_BLIND = 100;
    private static final int BIG_BLIND = 200;
    private static final String STYLE_YELLOW_BUTTON = "yellow-button";
    private static final String STYLE_GRAY_BUTTON = "gray-button";
    private static final String STYLE_RED_BUTTON = "red-button";
    private static final String STYLE_YELLOW_INPUT = "yellow-input-field";
    private static final String STYLE_GRAY_INPUT = "gray-input-field";
    private static final String STYLE_RED_INPUT = "red-input-field";
    private static final double SCALE_NORMAL = 1.0;
    private static final int DEALER_OFFSET = 3;
    private boolean inputActionAllowed;
    private int lastReferenceBet = BIG_BLIND;

    /** Standard constructor. Used by FXML. */
    public TaskbarController() {
        // default constructor for FXML
    }

    /**
     * Updates the state of the basic action buttons (Bet, Call, Fold, Raise) and the input field.
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
     * Sets the given button to a disabled state with red styling, indicating that the player is
     * out.
     *
     * @param b The button to be styled as red and disabled.
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
     * is out.
     *
     * @param t The text field to be styled as red and disabled.
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
     * the player's turn.
     *
     * @param b The button to be activated and styled for the player's turn.
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
     * it is the player's turn.
     *
     * @param t The text field to be activated and styled for the player's turn.
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
     * not the player's turn.
     *
     * @param b The button to be deactivated and styled for non-active state.
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
     * it is not the player's turn.
     *
     * @param t The text field to be deactivated and styled for non-active state.
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
        if (taskbarInput != null) {
            taskbarInput
                    .textProperty()
                    .addListener((obs, oldValue, newValue) -> refreshBetInputUi());
        }

        setBetButtonVisible(false);
    }

    /**
     * Updates the taskbar based on the current game state and the player's status.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the players, their bets, the current phase and other relevant details
     *     needed to update the taskbar UI accurately based on the player's status and the game
     *     context.
     * @param myPlayerId The PlayerId object representing the current player, used to identify the
     *     player's status and update the taskbar UI accordingly based on whether it is their turn
     *     and whether they are out of the game.
     */
    public void update(GameState state, PlayerId myPlayerId) {

        if (state == null || state.players == null || myPlayerId == null) {
            LOGGER.warn("Cannot update taskbar: invalid input");
            return;
        }

        this.lastState = state;
        if (state.currentBet > 0) {
            lastReferenceBet = state.currentBet;
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
        boolean isGameFinished = isHandFinished(state);

        updateBasicButtons(isMyTurn, isOut || isGameFinished);
        applyActionAvailability(state, me, isMyTurn, isOut || isGameFinished);
        setMoney(me.getChips());
        refreshBetInputUi();
    }

    /**
     * Applies the availability of actions (Bet, Call, Raise, Fold) based on the current game state.
     *
     * @param state The current game state to evaluate action availability.
     * @param me The player object representing the current player, used to determine their chips
     *     and bet status.
     * @param isMyTurn Indicates whether it is currently the player's turn, which affects whether
     *     actions can be taken.
     * @param isOutOrFinished Indicates whether the player is out of the game (folded or all-in) or
     *     if the game is finished, which disables actions.
     */
    private void applyActionAvailability(
            GameState state, Player me, boolean isMyTurn, boolean isOutOrFinished) {
        inputActionAllowed = false;
        if (!isMyTurn || isOutOrFinished || state == null || me == null) {
            setBetButtonVisible(false);
            return;
        }

        int chips = Math.max(0, me.getChips());
        int toCall = getToCall(state, me);

        boolean forcedFoldOnly = chips <= 0 || toCall > chips;
        if (forcedFoldOnly) {
            setActionEnabled(betButton, false);
            setActionEnabled(callButton, false);
            setActionEnabled(raiseButton, false);
            setActionEnabled(foldButton, true);
            deactivateInputField(taskbarInput);
            setBetButtonVisible(false);
            return;
        }

        if (isFirstPreflopPlayerInputOnly(state, me)) {
            setActionEnabled(betButton, true);
            setActionEnabled(callButton, false);
            setActionEnabled(foldButton, false);
            setActionEnabled(raiseButton, false);
            activateInputField(taskbarInput);
            inputActionAllowed = true;
            return;
        }

        int callTarget = effectiveCallTarget(state);
        int raiseTarget = safeDouble(callTarget);
        ValidationResult callValidation = validateTarget(state, me, callTarget);
        ValidationResult raiseValidation = validateTarget(state, me, raiseTarget);
        boolean canCall = callValidation.valid;
        boolean canRaise = raiseValidation.valid;
        boolean canInput = canCall || canRaise;

        setActionEnabled(betButton, canInput);
        setActionEnabled(callButton, canCall);
        setActionEnabled(raiseButton, canRaise);
        setActionEnabled(foldButton, true);

        if (canInput) {
            activateInputField(taskbarInput);
            inputActionAllowed = true;
        } else {
            deactivateInputField(taskbarInput);
        }
    }

    /**
     * Safely doubles the given value while preventing integer overflow.
     *
     * @param value The integer value to be safely doubled.
     * @return The safely doubled value, or 0 if the input is non-positive, or Integer.MAX_VALUE if
     *     doubling would overflow.
     */
    private int safeDouble(int value) {
        if (value <= 0) {
            return 0;
        }
        if (value > Integer.MAX_VALUE / 2) {
            return Integer.MAX_VALUE;
        }
        return value * 2;
    }

    /**
     * Sets the visibility of the Bet button in the taskbar.
     *
     * @param visible A boolean indicating whether the Bet button should be visible (true) or hidden
     *     (false).
     */
    private void setBetButtonVisible(boolean visible) {
        if (betButton == null) {
            return;
        }
        betButton.setVisible(visible);
        betButton.setManaged(visible);
    }

    /**
     * Refreshes the user interface of the bet input field and the Bet button based on the current
     * game state and the validity of the input.
     */
    private void refreshBetInputUi() {
        if (!inputActionAllowed || taskbarInput == null || taskbarInput.isDisabled()) {
            setBetButtonVisible(false);
            return;
        }

        ValidationResult validation = validateTypedAmount(lastState, taskbarInput.getText());
        boolean valid = validation.valid;
        setBetButtonVisible(valid);

        if (valid) {
            activateButton(betButton);
        } else {
            deactivateButton(betButton);
        }
    }

    /**
     * Enables or disables the given button based on the provided boolean value.
     *
     * @param button The Button object to be enabled or disabled based on the provided boolean
     *     value.
     * @param enabled A boolean value indicating whether the button should be enabled (true) or
     *     disabled (false).
     */
    private void setActionEnabled(Button button, boolean enabled) {
        if (enabled) {
            activateButton(button);
        } else {
            deactivateButton(button);
        }
    }

    /**
     * Calculates the amount needed to call the current bet in the game.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the current bet and the player's bet status.
     * @param me The Player object representing the current player, used to determine how much they
     *     have already invested in the current bet.
     * @return An integer representing the amount needed for the player to call the current bet.
     */
    private int getToCall(GameState state, Player me) {
        int current = effectiveCallTarget(state);
        int alreadyInvested = Math.max(0, me.getBet());
        return Math.max(0, current - alreadyInvested);
    }

    /**
     * Determines the effective call target for the current game state. If there is an active
     * current bet, it returns that as the call target.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the current bet and the last reference bet.
     * @return An integer representing the effective call target for the current game state.
     */
    private int effectiveCallTarget(GameState state) {
        if (state != null && state.currentBet > 0) {
            return state.currentBet;
        }
        return Math.max(0, lastReferenceBet);
    }

    /**
     * Checks if the current player is the first to act in the pre-flop phase and if the initial
     * blind layout is still in place.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the phase of the game, the players, the dealer position, and the active
     *     player.
     * @param me The Player object representing the current player, used to determine their position
     *     in the player list and whether they are the active player.
     * @return A boolean value indicating whether the current player is the first to act in the
     *     pre-flop phase with only the initial blind layout in place.
     */
    private boolean isFirstPreflopPlayerInputOnly(GameState state, Player me) {
        if (state == null || me == null || !isPreflop(state.phase) || state.players == null) {
            return false;
        }

        int size = state.players.size();
        if (size < 2) {
            return false;
        }

        int myIndex = state.players.indexOf(me);
        if (myIndex < 0) {
            return false;
        }

        int dealer = state.dealer;
        int firstIndex = (size == 2) ? dealer : (dealer + DEALER_OFFSET) % size;
        if (state.activePlayer != firstIndex || myIndex != firstIndex) {
            return false;
        }

        return isInitialPreflopBlindLayout(state);
    }

    /**
     * Checks if the initial blind layout is still in place during the pre-flop phase.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the players and their bets.
     * @return A boolean value indicating whether the initial blind layout is still in place during
     *     the pre-flop phase.
     */
    private boolean isInitialPreflopBlindLayout(GameState state) {
        int sbCount = 0;
        int bbCount = 0;
        int zeroCount = 0;

        for (Player player : state.players) {
            if (player == null) {
                continue;
            }

            int bet = Math.max(0, player.getBet());
            if (bet == SMALL_BLIND) {
                sbCount++;
            } else if (bet == BIG_BLIND) {
                bbCount++;
            } else if (bet == 0) {
                zeroCount++;
            }
        }

        int playerCount = state.players.size();
        return playerCount >= 2 && sbCount == 1 && bbCount == 1 && zeroCount == playerCount - 2;
    }

    /**
     * Checks if the given phase string corresponds to the pre-flop phase of the game.
     *
     * @param phase The string representing the current phase of the game, which is expected to be
     *     compared against "PREFLOP" to determine if it is the pre-flop phase.
     * @return A boolean value indicating whether the given phase string corresponds to the pre-flop
     *     phase.
     */
    private boolean isPreflop(String phase) {
        return "PREFLOP".equalsIgnoreCase(phase);
    }

    /**
     * Checks if the given phase string corresponds to the flop phase of the game.
     *
     * @param phase The string representing the current phase of the game, which is expected to be
     *     compared against "FLOP" to determine if it is the flop phase.
     * @return A boolean value indicating whether the given phase string corresponds to the flop
     *     phase.
     */
    private boolean isFlop(String phase) {
        return "FLOP".equalsIgnoreCase(phase);
    }

    /**
     * Checks if the given phase string corresponds to either the turn or river phase of the game.
     *
     * @param phase The string representing the current phase of the game, which is expected to be
     *     compared against "TURN" and "RIVER" to determine if it is either the turn or river phase.
     * @return A boolean value indicating whether the given phase string corresponds to either the
     *     turn or river phase.
     */
    private boolean isTurnOrRiver(String phase) {
        return "TURN".equalsIgnoreCase(phase) || "RIVER".equalsIgnoreCase(phase);
    }

    /**
     * Checks if the hand is finished based on the current game state.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information
     * @return A boolean value indicating whether the hand is finished, which can be determined by
     *     checking if the phase is "FINISHED" or "SHOWDOWN",
     */
    private boolean isHandFinished(GameState state) {
        if (state == null) {
            return false;
        }

        if ("FINISHED".equalsIgnoreCase(state.phase) || "SHOWDOWN".equalsIgnoreCase(state.phase)) {
            return true;
        }

        return state.players != null
                && state.winnerIndex >= 0
                && state.winnerIndex < state.players.size();
    }

    /**
     * Evaluates the risk level of a proposed bet based on the current game state, the amount of the
     * bet and the player's available chips.
     */
    private enum BetRisk {
        ALLOWED,
        WARNING,
        BLOCKED
    }

    /** Enum representing the types of actions that can be submitted from the taskbar. */
    private enum ActionType {
        INPUT,
        CALL_BUTTON,
        RAISE_BUTTON
    }

    /** Class representing the result of validating a proposed bet or action. */
    private static final class ValidationResult {
        private final boolean valid;
        private final boolean warning;
        private final String message;

        private ValidationResult(boolean valid, boolean warning, String message) {
            this.valid = valid;
            this.warning = warning;
            this.message = message;
        }

        private static ValidationResult ok() {
            return new ValidationResult(true, false, null);
        }

        private static ValidationResult warning(String message) {
            return new ValidationResult(true, true, message);
        }

        private static ValidationResult blocked(String message) {
            return new ValidationResult(false, false, message);
        }
    }

    /**
     * Submits a player action based on the specified ActionType.
     *
     * @param actionType The type of action being submitted, which can be an input-based action
     *     (where the player types an amount) or a specific button action for calling or raising.
     */
    private void submitAction(ActionType actionType) {
        if (gameService == null) {
            LOGGER.error("GameService not initialized");
            return;
        }

        GameState state = ensureLatestStateForAction(actionType.name().toLowerCase());
        if (state == null) {
            return;
        }

        if (isHandFinished(state)) {
            LOGGER.info("Action {} ignored: hand is already finished", actionType);
            update(state, myPlayerId);
            return;
        }

        Player me = findCurrentPlayer(state);
        if (me == null) {
            LOGGER.error("Action {} blocked: player missing in state", actionType);
            return;
        }

        int callTarget = effectiveCallTarget(state);
        int targetBet;
        if (actionType == ActionType.CALL_BUTTON) {
            targetBet = callTarget;
        } else if (actionType == ActionType.RAISE_BUTTON) {
            targetBet = safeDouble(callTarget);
        } else {
            Integer fromInput = parseInputTarget(taskbarInput.getText());
            if (fromInput == null) {
                return;
            }
            targetBet = fromInput;
        }

        ValidationResult validation = validateTarget(state, me, targetBet);
        if (!validation.valid) {
            refreshBetInputUi();
            return;
        }

        if (validation.warning) {
            showWarningDialog("WARNING", validation.message);
        }

        if (state.currentBet <= 0) {
            gameService.bet(targetBet);
            LOGGER.info("Player BET (target={})", targetBet);
        } else if (targetBet == callTarget) {
            gameService.call();
            LOGGER.info("Player CALL (target={})", targetBet);
        } else {
            int raiseBy = Math.max(0, targetBet - state.currentBet);
            gameService.raise(raiseBy);
            LOGGER.info("Player RAISE by {} (target={})", raiseBy, targetBet);
        }

        if (targetBet > 0) {
            lastReferenceBet = targetBet;
        }

        taskbarInput.clear();
        refreshGame();
    }

    /**
     * Ensures that the latest game state is available for processing a player action. If the last
     * known state is null, it attempts to fetch the current state from the GameService.
     *
     * @param text The text associated with the action being processed, used for logging purposes to
     *     indicate which action is being attempted when the state is not available.
     * @return The latest GameState object if available, or null if the state cannot be retrieved,
     *     indicating that the action cannot be processed.
     */
    private Integer parseInputTarget(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Finds the current player in the given game state based on the player's ID.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     a list of players.
     * @param text The text associated with the action being processed, used for logging purposes to
     *     indicate which action is being attempted when the player cannot be found in the state.
     * @return The Player object representing the current player if found in the game state, or null
     *     if no matching player is found, indicating that the player cannot be identified in the
     *     current game state.
     */
    private ValidationResult validateTypedAmount(GameState state, String text) {
        Integer target = parseInputTarget(text);
        if (target == null || state == null) {
            return ValidationResult.blocked("Invalid input");
        }
        Player me = findCurrentPlayer(state);
        if (me == null) {
            return ValidationResult.blocked("Player not found");
        }
        return validateTarget(state, me, target);
    }

    /**
     * Validates a proposed target bet against the current game state and the player's status. It
     * checks if the target bet is either a valid call or a valid raise (specifically, exactly
     * double the call target).
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the current bet, the phase of the game, and the players.
     * @param me The Player object representing the current player, used to determine their current
     *     bet, available chips, and to evaluate whether the proposed target bet is valid for this
     *     player based on their status in the game.
     * @param targetBet The integer value representing the proposed target bet that the player
     *     intends to make.
     * @return A ValidationResult object indicating whether the proposed target bet is valid, if it
     *     triggers a warning, or if it is blocked due to being invalid or too risky.
     */
    private ValidationResult validateTarget(GameState state, Player me, int targetBet) {
        if (state == null || me == null) {
            return ValidationResult.blocked("Invalid game state");
        }

        int callTarget = effectiveCallTarget(state);
        int raiseTarget = safeDouble(callTarget);
        if (targetBet != callTarget && targetBet != raiseTarget) {
            return ValidationResult.blocked("Only calls or exactly two raises allowed");
        }

        int alreadyInvested = Math.max(0, me.getBet());
        int required = Math.max(0, targetBet - alreadyInvested);
        int chips = Math.max(0, me.getChips());

        if (targetBet == callTarget && required == 0) {
            return ValidationResult.ok();
        }

        if (required <= 0) {
            return ValidationResult.blocked("Invalid bet");
        }

        if (required > chips) {
            return ValidationResult.blocked("Not enough chips for the next bet");
        }

        BetRisk risk = evaluateBetRisk(state, required, chips);
        if (risk == BetRisk.BLOCKED) {
            return ValidationResult.blocked("Assignment for this phase is blocked");
        }
        if (risk == BetRisk.WARNING) {
            return ValidationResult.warning(warningTextForPhase(state));
        }
        return ValidationResult.ok();
    }

    /**
     * Generates a warning message based on the current phase of the game.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the phase of the game.
     * @return A string containing the warning message appropriate for the current phase of the game
     *     when a player's bet triggers a warning due to being at least 50% of their stack.
     */
    private String warningTextForPhase(GameState state) {
        String phase = state != null ? state.phase : null;
        if (isFlop(phase)) {
            return "WARNING: On the flop, you're betting at least 50% of your stack.";
        }
        if (isTurnOrRiver(phase)) {
            return "WARNING: On the flop, you're betting at least 50% of your stack.";
        }
        return "WARNING: High stakes.";
    }

    /**
     * Evaluates the risk level of a proposed bet based on the current game state, the amount of the
     * bet, and the player's available chips.
     *
     * @param state The current GameState object representing the state of the game, which includes
     *     information about the phase of the game.
     * @param amount The integer value representing the amount of the proposed bet that the player
     *     intends to make.
     * @param chips The integer value representing the player's available chips.
     * @return A BetRisk enum value indicating the risk level of the proposed bet.
     */
    private BetRisk evaluateBetRisk(GameState state, int amount, int chips) {
        if (chips <= 0 || amount <= 0) {
            return BetRisk.BLOCKED;
        }

        double ratio = (double) amount / chips;
        String phase = state != null ? state.phase : null;

        if (isPreflop(phase)) {
            if (ratio > PREFLOP_BLOCK_RATIO) {
                return BetRisk.BLOCKED;
            }
            return BetRisk.ALLOWED;
        }

        if (isFlop(phase)) {
            if (ratio > FLOP_BLOCK_RATIO) {
                return BetRisk.BLOCKED;
            }
            if (ratio >= FLOP_WARN_RATIO) {
                return BetRisk.WARNING;
            }
            return BetRisk.ALLOWED;
        }

        if (isTurnOrRiver(phase)) {
            if (ratio >= TURN_RIVER_BLOCK_RATIO) {
                return BetRisk.BLOCKED;
            }
            if (ratio > TURN_RIVER_WARN_RATIO) {
                return BetRisk.WARNING;
            }
            return BetRisk.ALLOWED;
        }

        return BetRisk.ALLOWED;
    }

    /**
     * Displays a warning dialog with the specified title and content.
     *
     * @param title The string representing the title of the warning dialog, which is displayed in
     *     the title bar of the dialog window.
     * @param content The string representing the content of the warning message, which is displayed
     *     in the body of the dialog.
     */
    private void showWarningDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
            ValidationResult validation = validateTypedAmount(lastState, taskbarInput.getText());
            if (!validation.valid) {
                event.consume();
                setBetButtonVisible(false);
                return;
            }
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
        submitPresetInputAndProcess("call");
    }

    /** Called when the Fold button is clicked. */
    @FXML
    private void onInputPlayerFold() {

        if (gameService == null) {
            LOGGER.error("GameService not initialized");
            return;
        }

        GameState state = ensureLatestStateForAction("fold");
        if (isHandFinished(state)) {
            LOGGER.info("Fold ignored: hand is already finished");
            update(state, myPlayerId);
            return;
        }

        gameService.fold();
        LOGGER.info("Player FOLD");

        refreshGame();
    }

    /** Called when the Raise button is clicked. */
    @FXML
    private void onInputPlayerRaise() {
        submitPresetInputAndProcess("raise");
    }

    /**
     * Submits a preset input for either calling or raising based on the specified mode.
     *
     * @param mode A string indicating the mode of the action, which can be "call" for calling the
     *     current bet or "raise" for raising to double the current bet.
     */
    private void submitPresetInputAndProcess(String mode) {
        GameState state = ensureLatestStateForAction(mode);
        if (state == null || taskbarInput == null) {
            return;
        }

        int callTarget = effectiveCallTarget(state);
        int target = "raise".equals(mode) ? safeDouble(callTarget) : callTarget;
        taskbarInput.setText(String.valueOf(target));
        processBet();
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
        submitAction(ActionType.INPUT);
    }

    private Player findCurrentPlayer(GameState state) {
        if (state == null || state.players == null || myPlayerId == null) {
            return null;
        }

        return state.players.stream()
                .filter(player -> player != null && myPlayerId.equals(player.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Ensures that the latest game state is available for processing a player action.
     *
     * @param actionName The name of the action being processed, used for logging purposes to
     *     indicate which action is being attempted when the state is not available.
     * @return The latest GameState object if available, or null if the state cannot be retrieved,
     *     indicating that the action cannot be processed due to the lack of a valid game state.
     */
    private GameState ensureLatestStateForAction(String actionName) {
        try {
            GameState refreshed = gameService.refresh();
            if (refreshed != null) {
                lastState = refreshed;
                return refreshed;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to refresh game state for {}: {}", actionName, e.getMessage());
        }

        return lastState;
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

    /** Opens the highscore popup window from the taskbar. */
    @FXML
    private void onHighscoreButtonClick() {
        try {
            var shared = ClientApp.getSharedClientService();
            if (shared == null || shared.isOffline()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("No active server connection for highscores.");
                alert.showAndWait();
                return;
            }

            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/ui-structure/gameuicomponents/HighscoreView.fxml"));
            javafx.scene.Parent root = loader.load();

            HighscoreViewController controller = loader.getController();
            controller.setLobbyClient(new LobbyClient(shared));
            controller.refreshHighscores();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Casono Highscores");
            javafx.scene.image.Image icon =
                    new javafx.scene.image.Image(
                            getClass().getResource("/images/logoinverted.png").toExternalForm());
            stage.getIcons().add(icon);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            stage.setAlwaysOnTop(true);
            stage.toFront();
        } catch (Exception e) {
            LOGGER.error("Could not open highscore window from taskbar: {}", e.getMessage());
        }
    }
}
