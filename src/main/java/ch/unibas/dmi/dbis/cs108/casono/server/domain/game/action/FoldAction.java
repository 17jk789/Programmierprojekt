package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * FoldAction represents the action of a player folding in a poker game. When a
 * player folds, they forfeit their hand and are no longer active in the current
 * round. This action updates the player's status to indicate that they have
 * folded.
 */
public class FoldAction extends AbstractAction {

    /**
     * Constructs a FoldAction for the specified player ID.
     *
     * @param playerId The ID of the player performing the fold action.
     */
    public FoldAction(PlayerId playerId) {
        super(playerId);
    }

    /**
     * Retrieves the type of this action, which is FOLD.
     *
     * @return The ActionType corresponding to this action.
     */
    @Override
    public ActionType getType() {
        return ActionType.FOLD;
    }

    /**
     * Executes the fold action on the given game state. This method updates the
     * player's status to indicate that they have folded.
     *
     * @param state The current game state on which to execute the action.
     */
    @Override
    public void execute(GameState state) {
        Player player = state.getPlayer(playerId);
        player.setFolded(true);
    }
}
