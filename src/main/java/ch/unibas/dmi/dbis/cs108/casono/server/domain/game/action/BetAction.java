package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * Represents a bet action where a player contributes a fixed amount of chips. The amount is
 * deducted from the player's chips, added to the pot, and both the player's current bet and the
 * table's current bet are set to this amount.
 */
public class BetAction extends AbstractAction {

    private final int amount;

    /**
     * Constructs a BetAction for the specified player ID and bet amount.
     *
     * @param playerId The ID of the player performing the bet action.
     * @param amount The amount of chips the player is betting.
     */
    public BetAction(PlayerId playerId, int amount) {
        super(playerId);
        this.amount = amount;
    }

    /**
     * Retrieves the amount of chips being bet in this action.
     *
     * @return The amount of chips being bet.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Retrieves the type of this action, which is BET.
     *
     * @return The ActionType corresponding to this action.
     */
    @Override
    public ActionType getType() {
        return ActionType.BET;
    }

    /**
     * Executes the bet action on the given game state. This method updates the player's chip count,
     * adds the bet to the pot, and updates the current bet for the player in the game state.
     *
     * @param state The current game state on which to execute the action.
     */
    @Override
    public void execute(GameState state) {

        Player player = state.getPlayer(playerId);

        player.removeChips(amount);

        state.addToPot(amount);
        state.setCurrentBet(playerId, amount);

        state.getTableState().setCurrentBet(amount);
    }
}
