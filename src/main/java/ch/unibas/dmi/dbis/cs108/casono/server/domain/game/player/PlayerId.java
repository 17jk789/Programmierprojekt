package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player;

import java.util.Objects;

/**
 * PlayerId is a value object that represents the unique identifier of a player
 * in the game. It encapsulates a string value and provides validation to ensure
 * that it is not null. The PlayerId class also includes a factory method for
 * creating instances and overrides the toString method for easy representation.
 */
public record PlayerId(String value) {

    /**
     * Constructs a PlayerId with the specified value. The constructor validates
     * that the value is not null.
     *
     * @param value the string value representing the player's unique identifier
     * @throws NullPointerException if the value is null
     */
    public PlayerId {
        Objects.requireNonNull(value, "PlayerId cannot be null");
    }

    /**
     * Factory method to create a PlayerId instance from a string value.
     *
     * @param value the string value representing the player's unique identifier
     * @return a new PlayerId instance with the specified value
     */
    public static PlayerId of(String value) {
        return new PlayerId(value);
    }

    /**
     * Returns the string representation of the PlayerId, which is the encapsulated
     * value.
     *
     * @return the string value of the PlayerId
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Compares this PlayerId with another object for equality.
     *
     * @param o the object to compare to
     * @return true if this PlayerId is equal to the given object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayerId playerId = (PlayerId) o;
        return Objects.equals(value, playerId.value);
    }

    /**
     * Returns the hash code for this PlayerId.
     *
     * @return the hash code of the PlayerId
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
