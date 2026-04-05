package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Deck class represents a standard deck of playing cards. It provides functionality to create a
 * full deck of 52 cards, shuffle the deck, and draw cards from it. The deck is initialized with all
 * combinations of suits and ranks, and can be manipulated through its methods.
 */
public class Deck {

    private List<Card> cards;

    /**
     * Constructs a new Deck object and initializes it with a standard set of 52 playing cards. The
     * deck is created by iterating through all suits and ranks, creating a Card object for each
     * combination, and adding it to the list of cards.
     */
    public Deck() {

        cards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * Shuffles the deck of cards using the Collections.shuffle method, which randomly permutes the
     * list of cards. This method can be called to randomize the order of the cards in the deck
     * before drawing.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws and removes the last card in the list, which represents the top of the deck. This
     * method removes and returns the last card in the list of cards, which represents the top of
     * the deck. If the deck is empty, this method will throw an IndexOutOfBoundsException.
     *
     * @return The Card object that was drawn from the deck.
     */
    public Card draw() {
        return cards.remove(cards.size() - 1);
    }

    /**
     * Retrieves the current list of cards in the deck. This method returns a new LinkedList
     * containing the cards, which allows for safe manipulation of the returned list without
     * affecting the internal state of the deck.
     *
     * @return A List of Card objects currently in the deck.
     */
    public void setCards(List<Card> cards) {
        this.cards = new LinkedList<>(cards);
    }
}
