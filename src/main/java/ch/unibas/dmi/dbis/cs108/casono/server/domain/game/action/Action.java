package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;;

/**
 * The Action interface defines the structure for all player actions in the
 * poker game. Each action must specify its type, provide an implementation for
 * executing the action on the game state, and identify the player performing
 * the action.
 */
public interface Action {
    ActionType getType();

    void execute(GameState state);

    PlayerId getPlayerId();
}
