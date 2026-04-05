package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * The Player class represents a participant in the poker game. It holds information about the
 * player's identity, chip count, status, and hand of cards. The class provides methods for managing
 * the player's chips, hand, and status during the game.
 */
public class Player {

    private PlayerId id;
    private int chips;
    private PlayerStatus status;
    private boolean folded = false;

    private List<Card> hand = new ArrayList<>();

    /**
     * Constructs a Player with the specified ID and initial chip count. The player's status is set
     * to ACTIVE by default.
     *
     * @param id The unique identifier for the player.
     * @param chips The initial number of chips the player has.
     */
    public Player(PlayerId id, int chips) {
        this.id = id;
        this.chips = chips;
        this.status = PlayerStatus.ACTIVE;
    }

    /**
     * Checks if the player is all-in, meaning they have no chips left to bet.
     *
     * @return true if the player is all-in, false otherwise.
     */
    public boolean isAllIn() {
        return chips == 0;
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
     * Returns the display name of the player. Currently identical to the player ID.
     *
     * @return The player's name.
     */
    public String getName() {
        return id.value();
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
     * Removes a specified amount of chips from the player's total. If the amount exceeds the
     * player's current chips, it sets the chip count to zero.
     *
     * @param amount The number of chips to remove from the player.
     */
    public void removeChips(int amount) {
        chips -= amount;
        if (chips < 0) {
            chips = 0;
        }
    }

    /**
     * Adds a specified amount of chips to the player's total.
     *
     * @param amount The number of chips to add to the player.
     */
    public void addChips(int amount) {
        chips += amount;
    }

    /**
     * Sets the player's status to the specified value.
     *
     * @param status The new status for the player.
     */
    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the current status of the player.
     *
     * @return The player's status.
     */
    public PlayerStatus getStatus() {
        return status;
    }

    /**
     * Retrieves the player's current hand of cards.
     *
     * @return A list of Card objects representing the player's hand.
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card The Card object to be added to the player's hand.
     */
    public void giveCard(Card card) {
        hand.add(card);
    }

    /** Clears the player's hand of cards, removing all cards from the hand. */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Checks if the player has folded in the current round.
     *
     * @return true if the player has folded, false otherwise.
     */
    public boolean isFolded() {
        return folded;
    }

    /**
     * Sets the player's folded status to the specified value.
     *
     * @param folded The new folded status for the player.
     */
    public void setFolded(boolean folded) {
        this.folded = folded;
    }
}
