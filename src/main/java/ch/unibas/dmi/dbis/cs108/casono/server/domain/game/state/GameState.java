package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The GameState class encapsulates the entire state of a poker game at any
 * given
 * moment. It maintains information about the players, the pot, the current
 * phase of the game, the dealer position, and the cards in play. This class is
 * central to managing the flow of the game and ensuring that all actions and
 * decisions are based on an accurate representation of the current game state.
 */
public class GameState {

    private Map<PlayerId, Player> players = new HashMap<>();

    private List<PlayerId> playerOrder = new ArrayList<>();

    private Pot pot = new Pot();

    private TableState tableState = new TableState();

    private int currentPlayerIndex;

    private boolean handActive;

    private boolean allowOutOfTurn;

    private GamePhase phase;

    private int dealerIndex;

    private Map<PlayerId, Integer> currentBets = new HashMap<>();

    private Map<PlayerId, Integer> playerBetCommitments = new HashMap<>();

    private Map<PlayerId, List<Card>> holeCards = new HashMap<>();

    private List<Card> communityCards = new ArrayList<>();

    // private final Set<PlayerId> foldedPlayers = new HashSet<>();

    private Deck deck;

    // Getter

    /**
     * Returns the list of players currently in the game.
     *
     * @return A list of Player objects representing the players in the game.
     */
    public Collection<Player> getPlayers() {
        return players.values();
    }

    /**
     * Returns the current player whose turn it is to act.
     *
     * @return The Player object representing the current player.
     */
    public Player getCurrentPlayer() {
        PlayerId id = playerOrder.get(currentPlayerIndex);
        return players.get(id);
    }

    /**
     * Returns the index of the current player in the players list.
     *
     * @return An integer representing the index of the current player.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Indicates whether a hand is currently active in the game.
     *
     * @return true if a hand is active, false otherwise.
     */
    public boolean isHandActive() {
        return handActive;
    }

    /**
     * Returns the current phase of the game (e.g., PREFLOP, FLOP, TURN, RIVER).
     *
     * @return The GamePhase enum value representing the current phase of the game.
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * Returns the current state of the table, including player statuses and
     * positions.
     *
     * @return A TableState object representing the current state of the table.
     */
    public TableState getTableState() {
        return tableState;
    }

    /**
     * Returns the current pot, which contains the total amount of chips bet by
     * players in the current hand.
     *
     * @return A Pot object representing the current pot.
     */
    public Pot getPot() {
        return pot;
    }

    /**
     * Returns the current deck of cards being used in the game.
     *
     * @return A Deck object representing the current deck of cards.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns the list of community cards currently on the table.
     *
     * @return A list of Card objects representing the community cards.
     */
    public List<Card> getCommunityCards() {
        return communityCards;
    }

    /**
     * Returns the hole cards for a specific player based on their ID.
     *
     * @param playerId The ID of the player whose hole cards are being requested.
     * @return A list of Card objects representing the player's hole cards, or an
     *         empty list if the player has no hole cards.
     */
    public List<Card> getHoleCards(PlayerId  playerId) {
        return holeCards.getOrDefault(playerId, new ArrayList<>());
    }

    /**
     * Returns the index of the dealer in the players list.
     *
     * @return An integer representing the index of the dealer.
     */
    public int getDealerIndex() {
        return dealerIndex;
    }

    /**
     * Returns a map of player IDs to their current hole cards.
     *
     * @return A map where the key is the player ID and the value is a list of
     *         Card objects representing the player's hole cards.
     */
    public Map<PlayerId, List<Card>> getPlayerCards() {
        return holeCards;
    }

    /**
     * Returns the number of players currently in the game.
     *
     * @return An integer representing the number of players in the game.
     */
    public int getPlayerCount() {
        return players.size();
    }

    // SETTER

    /**
     * Sets the index of the current player in the players list.
     *
     * @param index An integer representing the index of the current player.
     */
    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    /**
     * Sets whether a hand is currently active in the game.
     *
     * @param handActive A boolean value indicating whether a hand is active.
     */
    public void setHandActive(boolean handActive) {
        this.handActive = handActive;
    }

    /**
     * Sets the current phase of the game.
     *
     * @param phase The GamePhase enum value representing the new phase of the game.
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /**
     * Sets the index of the dealer in the players list.
     *
     * @param dealerIndex An integer representing the index of the dealer.
     */
    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    /**
     * Sets the current deck of cards being used in the game.
     *
     * @param deck A Deck object representing the new deck of cards to be used in
     *             the game.
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    /**
     * Adds a player to the game with the specified ID and initial chip count. This
     * method creates a new Player object, adds it to the list of players, and
     * initializes the player's current bet and bet commitment in the game state.
     *
     * @param id    The ID of the player to add.
     * @param chips The initial number of chips the player has.
     */
    public void addPlayer(PlayerId id, int chips) {

        Player player = new Player(id, chips);

        players.put(id, player);
        playerOrder.add(id);

        currentBets.put(id, 0);
        playerBetCommitments.put(id, 0);
    }

    // BETTING LOGIC

    /**
     * Retrieves the current bet amount for a specific player based on their ID.
     *
     * @param playerId The ID of the player whose current bet is being requested.
     * @return An integer representing the current bet amount for the specified
     *         player, or 0 if the player has not placed any bets.
     */
    public int getCurrentBet(PlayerId playerId) {
        return currentBets.getOrDefault(playerId, 0);
    }

    /**
     * Sets the current bet amount for a specific player based on their ID. This
     * method updates the currentBets map with the new bet amount for the specified
     * player.
     *
     * @param playerId The ID of the player whose current bet is being set.
     * @param amount   The new bet amount to be set for the specified player.
     */
    public void setCurrentBet(PlayerId playerId, int amount) {
        currentBets.put(playerId, amount);
    }

    /**
     * Resets the current bets for all players by clearing the currentBets map. This
     * method is typically called at the start of a new hand to ensure that all
     * players' bets are reset to zero.
     */
    public void resetBets() {
        currentBets.clear();
    }

    /**
     * Adds a specified amount to the pot. This method updates the total amount in
     * the pot by adding the given amount to it.
     *
     * @param amount The amount of chips to be added to the pot.
     */
    public void addToPot(int amount) {
        pot.add(amount);
    }

    /**
     * Returns whether out-of-turn actions are allowed in the game. Out-of-turn
     * actions refer to players being able to act when it is not their turn, which
     * can be a feature in some poker variants or game modes.
     *
     * @return true if out-of-turn actions are allowed, false otherwise.
     */
    public boolean isAllowOutOfTurn() {
        return allowOutOfTurn;
    }

    /**
     * Sets whether out-of-turn actions are allowed in the game. This method updates
     * the allowOutOfTurn flag, which determines if players can act when it is not
     * their turn.
     *
     * @param allowOutOfTurn A boolean value indicating whether out-of-turn actions
     *                       should be allowed in the game.
     */
    public void setAllowOutOfTurn(boolean allowOutOfTurn) {
        this.allowOutOfTurn = allowOutOfTurn;
    }

    // PLAYER HELPERS

    /**
     * Retrieves a player from the game based on their ID. This method searches the
     * list of players for a player with the specified ID and returns it. If no
     * player with the given ID is found, a RuntimeException is thrown.
     *
     * @param id The ID of the player to retrieve.
     * @return The Player object corresponding to the specified ID.
     * @throws RuntimeException if no player with the given ID is found in the game.
     */
    public Player getPlayer(PlayerId id) {
        Player player = players.get(id);

        if (player == null) {
            throw new RuntimeException("Player not found: " + id);
        }

        return player;
    }

    // BET COMMITMENTS

    /**
     * Retrieves the current bet commitment for a specific player based on their ID.
     * A bet commitment represents the total amount a player has committed to the
     * pot in the current hand, including all bets, raises, and calls they have
     * made.
     *
     * @param playerId The ID of the player whose current bet commitment is being
     *                 requested.
     * @return An integer representing the current bet commitment for the specified
     *         player, or 0 if the player has not made any bet commitments.
     */
    public int getCurrentBetCommitment(PlayerId playerId) {
        return playerBetCommitments.getOrDefault(playerId, 0);
    }

    /**
     * Sets the current bet commitment for a specific player based on their ID. This
     * method updates the playerBetCommitments map with the new bet commitment
     * amount for the specified player.
     *
     * @param playerId The ID of the player whose current bet commitment is being
     *                 set.
     * @param amount   The new bet commitment amount to be set for the specified
     *                 player.
     */
    public void setCurrentBetCommitment(PlayerId playerId, int amount) {
        playerBetCommitments.put(playerId, amount);
    }

    // CARDS

    /**
     * Gives hole cards to a specific player based on their ID. This method takes
     * two Card objects representing the player's hole cards and adds them to the
     * holeCards map under the player's ID.
     *
     * @param playerId The ID of the player to whom the hole cards are being given.
     * @param c1       The first Card object representing one of the player's hole
     *                 cards.
     * @param c2       The second Card object representing the other hole card for
     *                 the player.
     */
    public void giveHoleCards(PlayerId playerId, Card c1, Card c2) {

        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);

        holeCards.put(playerId, cards);
    }

    /**
     * Adds a community card to the game state. This method takes a Card object
     * representing a community card and adds it to the list of community cards on
     * the table.
     *
     * @param card The Card object representing the community card to be added to
     *             the
     *             game state.
     */
    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    /**
     * Resets the community cards by clearing the list of community cards. This
     * method is typically called at the start of a new hand to ensure that all
     * community cards from the previous hand are removed from the game state.
     */
    public void resetCommunityCards() {
        communityCards.clear();
    }

    // TURN MANAGEMENT

    /**
     * Advances the turn to the next player in the players list. This method updates
     * the currentPlayerIndex by incrementing it and wrapping around to the start of
     * the list if necessary, ensuring that the turn order is maintained correctly
     * throughout the game.
     */
    public void nextPlayer() {
        int start = currentPlayerIndex;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerOrder.size();

            Player p = getCurrentPlayer();

            if (!p.isFolded() && !p.isAllIn()) {
                return;
            }

        } while (currentPlayerIndex != start);
    }

    // Dealer Rotation

    /**
     * Rotates the dealer position to the next player in the players list. This
     * method updates the dealerIndex by incrementing it and wrapping around to the
     * start of the list if necessary, ensuring that the dealer position rotates
     * correctly after each hand.
     */
    public void rotateDealer() {
        dealerIndex = (dealerIndex + 1) % playerOrder.size();
    }

    /**
     * Retrieves the current dealer based on the dealerIndex. This method returns
     * the Player object corresponding to the current dealer position in the players
     * list.
     *
     * @return The Player object representing the current dealer.
     */
    public Player getDealer() {
        PlayerId id = playerOrder.get(dealerIndex);
        return players.get(id);
    }

    // HAND RESET

    /**
     * Starts a new hand by resetting the game state for the next round of poker.
     * This
     * method sets the handActive flag to true, resets the game phase to PREFLOP,
     * clears the pot, resets all player bets and bet commitments, clears the
     * community cards, and initializes a new shuffled deck of cards for the new
     * hand.
     */
    public void startNewHand() {

        handActive = true;
        phase = GamePhase.PREFLOP;

        pot = new Pot();

        resetBets();
        playerBetCommitments.clear();

        resetCommunityCards();
        holeCards.clear();

        for (Player player : players.values()) {
            player.setFolded(false);
        }

        deck = new Deck();
        deck.shuffle();

        currentPlayerIndex = (dealerIndex + 1) % playerOrder.size();
    }

    /**
     * Folds a player in the current hand. This method adds the specified player's ID
     * to the set of folded players, indicating that the player has folded and is
     * no longer active in the current hand.
     *
     * @param playerId The ID of the player who is folding.
     */
    public void foldPlayer(PlayerId playerId) {
        getPlayer(playerId).setFolded(true);
    }

    /**
     * Checks if a specific player has folded in the current hand. This method
     * checks if the specified player's ID is present in the set of folded players,
     * indicating that the player has folded.
     *
     * @param playerId The ID of the player to check for folding status.
     * @return true if the player has folded, false otherwise.
     */
    public boolean isFolded(PlayerId playerId) {
        return getPlayer(playerId).isFolded();
    }
}
