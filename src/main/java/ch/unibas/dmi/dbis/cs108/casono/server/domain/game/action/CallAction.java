package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * CallAction represents the action of a player calling in a poker game. When a player calls, they
 * match the current bet on the table by paying the difference between their current bet and the
 * table's current bet. This action updates the player's chip count, adds the required amount to the
 * pot, and updates the player's current bet accordingly.
 */
public class CallAction extends AbstractAction {

    /**
     * Constructs a CallAction for the specified player.
     *
     * @param playerId the ID of the player performing the call action
     */
    public CallAction(PlayerId playerId) {
        super(playerId);
    }

    /**
     * Returns the type of this action, which is CALL.
     *
     * @return the ActionType representing a call action
     */
    @Override
    public ActionType getType() {
        return ActionType.CALL;
    }

    /**
     * Executes the call action on the given game state. This method checks if the player is folded,
     * calculates the amount to call, updates the player's chip count, adds the amount to the pot,
     * and updates the player's current bet.
     *
     * @param state the current game state on which to execute the call action
     */
    @Override
    public void execute(GameState state) {

        Player player = state.getPlayer(playerId);

        if (player.isFolded()) {
            return;
        }

        int tableBet = state.getTableState().getCurrentBet();
        int playerBet = state.getCurrentBet(playerId);

        int toCall = tableBet - playerBet;

        if (toCall <= 0) {
            return;
        }

        player.removeChips(toCall);
        state.addToPot(toCall);

        state.setCurrentBet(playerId, playerBet + toCall);
    }
}
