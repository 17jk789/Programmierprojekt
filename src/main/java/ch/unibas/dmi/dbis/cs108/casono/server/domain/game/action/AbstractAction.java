package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;

/**
 * AbstractAction serves as a base class for all player actions in the poker
 * game.
 * It implements the Action interface and provides a common implementation for
 * retrieving the player ID associated with the action.
 */
public abstract class AbstractAction implements Action {
    protected final PlayerId playerId;

    /**
     * Constructs an AbstractAction with the specified player ID.
     *
     * @param playerId The ID of the player performing the action.
     */
    public AbstractAction(PlayerId playerId) {
        this.playerId = playerId;
    }

    /**
     * Retrieves the ID of the player performing the action.
     *
     * @return The player ID associated with this action.
     */
    @Override
    public PlayerId getPlayerId() {
        return playerId;
    }
}
