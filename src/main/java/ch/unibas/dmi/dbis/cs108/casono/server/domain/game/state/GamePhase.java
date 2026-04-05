package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

/**
 * The GamePhase enumeration represents the different phases of a poker game. Each phase corresponds
 * to a specific stage in the game, such as waiting for players, dealing cards, and determining the
 * winner. This enumeration is essential for managing the flow of the game and ensuring that actions
 * are performed at the appropriate times.
 */
public enum GamePhase {
    WAITING,
    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN,
    FINISHED
}
