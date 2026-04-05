package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;

/**
 * The TurnManager class is responsible for managing the flow of turns in a poker game. It provides
 * functionality to determine the next player in the sequence and update the game state accordingly.
 * The TurnManager ensures that players take their turns in the correct order, allowing for a smooth
 * and organized gameplay experience.
 */
public class TurnManager {

    /**
     * Advances the game state to the next player's turn. This method calculates the index of the
     * next player based on the current player index and the total number of players in the game. It
     * then updates the game state to reflect the new current player.
     *
     * @param state The current game state that will be updated to reflect the next player's turn.
     */
    public void nextPlayer(GameState state) {

        int next = (state.getCurrentPlayerIndex() + 1) % state.getPlayers().size();

        state.setCurrentPlayerIndex(next);
    }
}
