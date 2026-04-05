package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.Action;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The GameEngine class is responsible for managing the core logic of the poker game. It integrates
 * the RuleEngine, RoundManager, and TurnManager to orchestrate the flow of the game. The GameEngine
 * processes player actions, updates the game state accordingly, and ensures that the game
 * progresses through its various stages (pre-flop, flop, turn, river) while adhering to the rules
 * of poker.
 */
public class GameEngine {

    private final RuleEngine ruleEngine;
    private final RoundManager roundManager;
    private final TurnManager turnManager;

    private final GameState state;

    /**
     * Constructs a GameEngine with the specified game state, rule engine, round manager, and turn
     * manager.
     *
     * @param state The initial game state to be managed by the engine.
     * @param ruleEngine The RuleEngine instance responsible for validating player actions.
     * @param roundManager The RoundManager instance responsible for managing the progression of
     *     rounds.
     * @param turnManager The TurnManager instance responsible for managing player turns.
     */
    public GameEngine(
            GameState state,
            RuleEngine ruleEngine,
            RoundManager roundManager,
            TurnManager turnManager) {
        this.state = state;
        this.ruleEngine = ruleEngine;
        this.roundManager = roundManager;
        this.turnManager = turnManager;
    }

    /**
     * Starts a new hand with the provided game state. This method initializes the round manager to
     * begin a new hand and sets up the game state accordingly.
     *
     * @param state The game state to be used for starting the new hand.
     */
    public void startNewHand(GameState state) {
        roundManager.startNewHand(state);
    }

    /**
     * Starts a new hand using the current game state. This method is a convenience method that
     * calls the startNewHand method with the current state of the game.
     */
    public void startNewHand() {
        roundManager.startNewHand(state);
    }

    /**
     * Processes a player action by validating it against the game rules, executing the action,
     * progressing to the next player's turn, and checking if the round should progress to the next
     * stage.
     *
     * @param action The player action to be processed.
     */
    public void processAction(Action action) {
        handleAction(state, action);
    }

    /**
     * Handles a player action by performing the following steps: 1. Validates the action against
     * the game rules using the RuleEngine. 2. Executes the action, which updates the game state
     * accordingly. 3. Advances to the next player's turn using the TurnManager. 4. Checks if the
     * round should progress to the next stage (e.g., from pre-flop to flop) using the RoundManager.
     *
     * @param state The current game state on which to execute the action.
     * @param action The player action to be processed.
     */
    public void handleAction(GameState state, Action action) {

        // 1.Check the rules
        ruleEngine.validate(state, action);

        // 2. Perform action
        action.execute(state);

        // 3. Next turn
        turnManager.nextPlayer(state);

        // 4. Check loop logic
        roundManager.progressIfNeeded(state);
    }

    /**
     * Retrieves the current game state managed by the GameEngine.
     *
     * @return The current GameState instance.
     */
    public GameState getState() {
        return state;
    }
}
