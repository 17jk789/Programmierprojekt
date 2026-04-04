package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player;

/**
 * The PlayerStatus enumeration represents the various states a player can be in
 * during a poker game. It helps to track the player's current status, such as
 * whether they are actively participating in the hand, have folded, are all-in,
 * or have been eliminated from the game.
 */
public enum PlayerStatus {
    ACTIVE,
    FOLDED,
    ALL_IN,
    OUT
}
