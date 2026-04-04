package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action;

/**
 * ActionType is an enumeration that defines the various types of actions that
 * players can perform in a poker game. Each action type corresponds to a
 * specific move or decision that a player can make during their turn.
 */
public enum ActionType {
    FOLD,
    CALL,
    RAISE,
    CHECK,
    BET,
    ALL_IN,
    BLIND
}
