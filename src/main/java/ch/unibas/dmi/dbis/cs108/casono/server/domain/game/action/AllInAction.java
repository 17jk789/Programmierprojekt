package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * AllInAction represents the action of a player going all-in in a poker game. When a player goes
 * all-in, they bet all of their remaining chips. This action updates the player's chip count, adds
 * the bet to the pot, and updates the current bet for the player in the game state.
 */
public class AllInAction extends AbstractAction {

    /**
     * Constructs an AllInAction for the specified player ID.
     *
     * @param playerId The ID of the player performing the all-in action.
     */
    public AllInAction(PlayerId playerId) {
        super(playerId);
    }

    /**
     * Retrieves the type of this action, which is ALL_IN.
     *
     * @return The ActionType corresponding to this action.
     */
    @Override
    public ActionType getType() {
        return ActionType.ALL_IN;
    }

    /**
     * Executes the all-in action on the given game state. This method updates the player's chip
     * count, adds the bet to the pot, and updates the current bet for the player in the game state.
     *
     * @param state The current game state on which to execute the action.
     */
    @Override
    public void execute(GameState state) {
        Player player = state.getPlayer(playerId);
        int chips = player.getChips();

        player.removeChips(chips);
        state.addToPot(chips);
        state.setCurrentBet(playerId, state.getCurrentBet(playerId) + chips);
    }
}
