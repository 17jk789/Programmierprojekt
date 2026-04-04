package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

public class RaiseAction extends AbstractAction {

    private final int raiseAmount;

    /**
     * Constructs a RaiseAction for the specified player ID and raise amount.
     *
     * @param playerId    The ID of the player performing the raise action.
     * @param raiseAmount The amount of chips the player is raising.
     */
    public RaiseAction(PlayerId playerId, int raiseAmount) {
        super(playerId);
        this.raiseAmount = raiseAmount;
    }

    /**
     * Retrieves the amount of chips being raised in this action.
     *
     * @return The amount of chips being raised.
     */
    public int getAmount() {
        return raiseAmount;
    }

    /**
     * Retrieves the type of this action, which is RAISE.
     *
     * @return The ActionType corresponding to this action.
     */
    @Override
    public ActionType getType() {
        return ActionType.RAISE;
    }

    /**
     * Executes the raise action on the given game state. This method updates the
     * player's chip count, adds the raise amount to the pot, and updates the
     * current bet for the player in the game state.
     *
     * @param state The current game state on which to execute the action.
     */
    @Override
    public void execute(GameState state) {

        Player player = state.getPlayer(playerId);

        int alreadyPaid = state.getCurrentBet(playerId);
        int toCall = state.getTableState().getCurrentBet() - alreadyPaid;

        int total = toCall + raiseAmount;

        player.removeChips(total);
        state.addToPot(total);

        int newBet = state.getTableState().getCurrentBet() + raiseAmount;

        state.getTableState().setCurrentBet(newBet);
        state.setCurrentBet(playerId, alreadyPaid + total);
    }
}
