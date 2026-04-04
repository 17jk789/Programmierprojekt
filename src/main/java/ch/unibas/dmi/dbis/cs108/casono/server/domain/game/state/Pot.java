package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

/**
 * The Pot class represents the total amount of chips that players have bet in a
 * poker game. It provides methods to add chips to the pot, retrieve the
 * current amount, and reset the pot for a new round. This class is essential
 * for managing the betting aspect of the game and ensuring that the pot is
 * accurately maintained throughout the game.
 */
public class Pot {

    // Disabled because it caused the port to be set to 0
    // private int total;
    private int amount = 0;

    /**
     * Adds the specified amount of chips to the pot.
     *
     * @param chips The number of chips to add to the pot.
     */
    public void add(int chips) {
        amount += chips;
    }

    /**
     * Retrieves the current amount of chips in the pot.
     *
     * @return The current amount of chips in the pot.
     */
    public int getAmount() {
        return amount;
    }

    // Disabled because it caused the port to be set to 0
    // public int getTotal() {
    // return total;
    // }

    /**
     * Resets the pot to zero, typically used at the end of a round or when
     * starting a new game.
     */
    public void reset() {
        amount = 0;
    }
}
