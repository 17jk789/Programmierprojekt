package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

/**
 * The TableState class represents the current state of the poker table during a game. It keeps
 * track of the current bet, the last raise size, the minimum raise amount, and the big blind.
 * Additionally, it manages the betting status, including whether betting is currently open and if
 * it can be reopened after being closed. The class also tracks the last aggressor's ID to determine
 * who made the most recent aggressive action (like a raise) in the betting round.
 */
public class TableState {

    private int currentBet;
    private int lastRaiseSize;
    private int minRaise;
    private int bigBlind;

    private boolean bettingOpen;
    private boolean canReopenBetting;

    private String lastAggressorId;

    /**
     * Retrieves the current bet amount for the table. This value represents the highest bet that
     * has been placed in the current betting round and is used to determine how much players need
     * to call or raise to stay in the hand.
     *
     * @return The current bet amount for the table.
     */
    public int getCurrentBet() {
        return currentBet;
    }

    /**
     * Sets the current bet amount for the table. This method is typically called when a player
     * places a bet or raises, updating the current bet to reflect the new amount.
     *
     * @param currentBet The new current bet amount to set for the table.
     */
    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    /**
     * Returns the big blind amount. The big blind serves as a baseline for betting and is often
     * used to determine the minimum bet or raise.
     *
     * @return The big blind amount.
     */
    public int getBigBlind() {
        return bigBlind;
    }

    /**
     * Sets the big blind amount for the game. The big blind is a forced bet that players must post
     * before the cards are dealt, and it serves as a baseline for betting in the game.
     *
     * @param bigBlind The amount to set as the big blind for the game.
     */
    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }

    /**
     * Retrieves the minimum raise amount for the current betting round. The minimum raise is
     * typically determined by the size of the last raise or the big blind, ensuring that players
     * must raise by at least a certain amount.
     *
     * @return The minimum raise amount for the current betting round.
     */
    public int getMinRaise() {
        return minRaise;
    }

    /**
     * Sets the minimum raise amount for the current betting round. This method is typically called
     * after a raise is made, updating the minimum raise to ensure that subsequent raises meet the
     * required amount.
     *
     * @param minRaise The new minimum raise amount to set for the current betting round.
     */
    public void setMinRaise(int minRaise) {
        this.minRaise = minRaise;
    }

    /**
     * Checks if betting is currently open at the table. This status indicates whether players are
     * allowed to place bets or raises during the current betting round.
     *
     * @return true if betting is open, false otherwise.
     */
    public boolean isBettingOpen() {
        return bettingOpen;
    }

    /**
     * Sets the betting status for the table. This method can be used to open or close betting
     * during a betting round, controlling whether players can place bets or raises.
     *
     * @param bettingOpen The new betting status to set for the table (true for open, false for
     *     closed).
     */
    public void setBettingOpen(boolean bettingOpen) {
        this.bettingOpen = bettingOpen;
    }

    /**
     * Checks if betting can be reopened after being closed. This status allows for scenarios where
     * betting may be temporarily closed (e.g., after a raise) but can be reopened to allow other
     * players to respond.
     *
     * @return true if betting can be reopened, false otherwise.
     */
    public boolean canReopenBetting() {
        return canReopenBetting;
    }

    /**
     * Sets whether betting can be reopened after being closed. This method is typically called
     * after a raise is made, allowing for the possibility of reopening betting to let other players
     * respond to the raise.
     *
     * @param canReopenBetting The new status indicating whether betting can be reopened (true or
     *     false).
     */
    public void setCanReopenBetting(boolean canReopenBetting) {
        this.canReopenBetting = canReopenBetting;
    }

    /**
     * Retrieves the ID of the last aggressor in the current betting round. The last aggressor is
     * the player who made the most recent aggressive action (like a raise) and is important for
     * determining the flow of betting and who may have the opportunity to respond.
     *
     * @return The ID of the last aggressor in the current betting round.
     */
    public String getLastAggressorId() {
        return lastAggressorId;
    }

    /**
     * Sets the ID of the last aggressor in the current betting round. This method is typically
     * called after a player makes an aggressive action (like a raise), updating the last aggressor
     * ID to reflect the most recent aggressive action.
     *
     * @param lastAggressorId The new ID of the last aggressor to set for the current betting round.
     */
    public void setLastAggressorId(String lastAggressorId) {
        this.lastAggressorId = lastAggressorId;
    }
}
