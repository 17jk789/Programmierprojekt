package ch.unibas.dmi.dbis.cs108.casono.client.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the poker game, including their name, chip count, current bet, state
 * (e.g., "active", "folded"), and their hole cards.
 */
public class Player {

    private PlayerId id;
    private int chips;
    private int bet;
    private PlayerState state;

    private List<Card> cards = new ArrayList<>();

    /**
     * Constructs a new Player with default values. The player's state is set to ACTIVE by default.
     */
    public Player() {
        this.state = PlayerState.ACTIVE;
    }

    /**
     * Constructs a new Player with the specified ID and initial chip count. The player's state is
     * set to ACTIVE by default.
     *
     * @param id The unique identifier for the player.
     * @param chips The initial number of chips the player has.
     */
    public Player(PlayerId id, int chips) {
        this.id = id;
        this.chips = chips;
        this.bet = 0;
        this.state = PlayerState.ACTIVE;
    }

    /**
     * Retrieves the unique identifier of the player.
     *
     * @return The player's ID.
     */
    public PlayerId getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the player.
     *
     * @param id The player's ID to set.
     */
    public void setId(PlayerId id) {
        this.id = id;
    }

    /**
     * Returns the display name of the player. If the player's ID is not set, it returns an empty
     * string.
     *
     * @return The player's name.
     */
    public String getName() {
        return id != null ? id.value() : "";
    }

    /**
     * Retrieves the current chip count of the player.
     *
     * @return The number of chips the player has.
     */
    public int getChips() {
        return chips;
    }

    /**
     * Sets the current chip count of the player.
     *
     * @param chips The number of chips to set for the player.
     */
    public void setChips(int chips) {
        this.chips = chips;
    }

    /**
     * Retrieves the current bet amount of the player.
     *
     * @return The amount the player has currently bet in the ongoing hand.
     */
    public int getBet() {
        return bet;
    }

    /**
     * Sets the current bet amount of the player.
     *
     * @param bet The amount the player has currently bet in the ongoing hand to set.
     */
    public void setBet(int bet) {
        this.bet = bet;
    }

    /**
     * Retrieves the current state of the player (e.g., ACTIVE, FOLDED, ALL_IN).
     *
     * @return The player's current state in the game.
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * Sets the current state of the player (e.g., ACTIVE, FOLDED, ALL_IN).
     *
     * @param state The player's current state in the game to set.
     */
    public void setState(PlayerState state) {
        this.state = state;
    }

    /**
     * Checks if the player is currently the dealer.
     *
     * @return True if the player's state is DEALER, false otherwise.
     */
    public boolean isDealer() {
        return state == PlayerState.DEALER;
    }

    /**
     * Retrieves the list of hole cards currently held by the player.
     *
     * @return A list of Card objects representing the player's hole cards.
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Sets the list of hole cards currently held by the player.
     *
     * @param cards A list of Card objects representing the player's hole cards to set.
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Adds a card to the player's list of hole cards.
     *
     * @param card The Card object to be added to the player's hole cards.
     */
    public void addCard(Card card) {
        this.cards.add(card);
    }

    /**
     * Adds the specified amount of chips to the player's current chip count.
     *
     * @param amount The number of chips to add to the player's current chip count.
     */
    public void addChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.chips += amount;
    }

    /**
     * Removes the specified amount of chips from the player's current chip count.
     *
     * @param amount The number of chips to remove from the player's current chip count.
     */
    public void removeChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > this.chips) {
            throw new IllegalArgumentException(
                    "Not enough chips. Available: " + this.chips + ", Requested: " + amount);
        }
        this.chips -= amount;
    }

    /**
     * Sets the player's state to FOLDED, indicating that they have folded in the current hand.
     */
    public void fall() {
        this.state = PlayerState.FOLDED;
        if (this.id != null) {
            this.id = PlayerId.of(this.id.value() + " (Fall)");
        }
    }
}
