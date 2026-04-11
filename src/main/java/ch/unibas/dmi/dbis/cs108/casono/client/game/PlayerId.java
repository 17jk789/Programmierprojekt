package ch.unibas.dmi.dbis.cs108.casono.client.game;

import java.util.Objects;

/** Represents a unique identifier for a player in the client domain. */
public record PlayerId(String value) {

    /**
     * Constructs a PlayerId with the given string value, ensuring it is not null or empty.
     *
     * @param value the string value representing the player's unique identifier.
     */
    public PlayerId {
        Objects.requireNonNull(value, "PlayerId cannot be null");
        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("PlayerId cannot be empty");
        }
    }

    /** Factory method to create a PlayerId. */
    public static PlayerId of(String value) {
        return new PlayerId(value);
    }

    /**
     * Returns the string representation of the PlayerId, which is the encapsulated value.
     *
     * @return the string value of the PlayerId.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Compares this PlayerId with another object for equality.
     *
     * @param o the object to compare to.
     * @return true if this PlayerId is equal to the given object, false otherwise.
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
     * @return the hash code of the PlayerId.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
