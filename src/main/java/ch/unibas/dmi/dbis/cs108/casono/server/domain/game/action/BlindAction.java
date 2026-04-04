package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * Executes a blind by deducting chips from the player, adding them to the pot,
 * increasing the player's current bet, and updating the table's current bet
 * if the blind exceeds the existing bet.
 */
public class BlindAction implements Action {

    private final PlayerId playerId;
    private final int amount;

    /**
     * Constructs a BlindAction for the specified player ID and blind amount.
     *
     * @param playerId The ID of the player posting the blind bet.
     * @param amount   The amount of chips the player is posting as a blind bet.
     */
    public BlindAction(PlayerId playerId, int amount) {
        this.playerId = playerId;
        this.amount = amount;
    }

    /**
     * Retrieves the type of this action, which is BLIND.
     *
     * @return The ActionType corresponding to this action.
     */
    @Override
    public ActionType getType() {
        return ActionType.BLIND;
    }

    /**
     * Retrieves the ID of the player performing the action.
     *
     * @return The player ID associated with this action.
     */
    @Override
    public PlayerId  getPlayerId() {
        return playerId;
    }

    /**
     * Executes the blind action on the given game state. This method updates the
     * player's chip count, adds the blind bet to the pot, and updates the current
     * bet for the player in the game state.
     *
     * @param state The current game state on which to execute the action.
     */
    @Override
    public void execute(GameState state) {

        Player player = state.getPlayer(playerId);

        player.removeChips(amount);

        state.addToPot(amount);

        int alreadyPaid = state.getCurrentBet(playerId);

        state.setCurrentBet(playerId, alreadyPaid + amount);

        if (state.getTableState().getCurrentBet() < amount) {
            state.getTableState().setCurrentBet(amount);
        }
    }

    /**
     * Retrieves the amount of chips being posted as a blind bet in this action.
     *
     * @return The amount of chips being posted as a blind bet.
     */
    public int getAmount() {
        return amount;
    }
}
