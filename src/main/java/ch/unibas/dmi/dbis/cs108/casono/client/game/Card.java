package ch.unibas.dmi.dbis.cs108.casono.client.game;

/** Represents a playing card with a value and suit. */
public class Card {
    private String value;
    private String suit;

    /**
     * Constructs a Card with the specified value and suit.
     *
     * @param value The value of the card (e.g., 2, 3, ..., 10, J, Q, K, A).
     * @param suit The suit of the card (e.g., Hearts, Diamonds, Clubs, Spades).
     */
    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    /**
     * Retrieves the value of the card.
     *
     * @return The value of the card.
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the suit of the card.
     *
     * @return The suit of the card.
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Sets the value of the card.
     *
     * @param value The value to set for the card.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the suit of the card.
     *
     * @param suit The suit to set for the card.
     */
    public void setSuit(String suit) {
        this.suit = suit;
    }
}
