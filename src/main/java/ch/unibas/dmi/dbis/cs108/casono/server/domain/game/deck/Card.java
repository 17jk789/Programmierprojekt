package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck;

/**
 * The Card class represents a single playing card in a standard deck of cards.
 * Each card has a suit (Hearts, Diamonds, Clubs, Spades) and a rank (2-10,
 * Jack, Queen, King, Ace).
 * This class provides methods to retrieve the suit and rank of the card, as
 * well as a string representation of the card.
 */
public class Card {

    private Suit suit;
    private Rank rank;

    /**
     * Constructs a Card with the specified suit and rank.
     *
     * @param suit The suit of the card (Hearts, Diamonds, Clubs, Spades).
     * @param rank The rank of the card (2-10, Jack, Queen, King, Ace).
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Retrieves the suit of the card.
     *
     * @return The suit of the card.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Retrieves the rank of the card.
     *
     * @return The rank of the card.
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Returns a string representation of the card, combining its rank and suit.
     *
     * @return A string representation of the card (e.g., "Ace of Spades").
     */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
