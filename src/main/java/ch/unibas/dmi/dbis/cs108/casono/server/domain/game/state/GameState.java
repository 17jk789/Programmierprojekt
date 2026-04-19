package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The GameState class encapsulates the entire state of a poker game at any given moment.
 *
 * <p>Important invariants this implementation maintains:
 *
 * <ul>
 *   <li>{@code playerOrder} defines the stable seating/turn order.
 *   <li>{@code currentBets} always contains an entry for every player in {@code playerOrder}.
 *   <li>{@code holeCards} maps every player to a mutable list; if not present, it is created on
 *       demand.
 * </ul>
 */
public class GameState {

    private final Map<PlayerId, Player> players = new HashMap<>();
    private final List<PlayerId> playerOrder = new ArrayList<>();

    private Pot pot = new Pot();
    private final TableState tableState = new TableState();

    private int currentPlayerIndex;
    private boolean handActive;
    private boolean allowOutOfTurn;
    private GamePhase phase;
    private int dealerIndex;

    private final Map<PlayerId, Integer> currentBets = new HashMap<>();
    private final Map<PlayerId, Integer> playerBetCommitments = new HashMap<>();
    private final Set<PlayerId> actedThisRound = new HashSet<>();

    private final Map<PlayerId, List<Card>> holeCards = new HashMap<>();
    private final List<Card> communityCards = new ArrayList<>();

    private Deck deck;

    private static final int DEALER_OFFSET = 3;

    /**
     * Returns the players in seating/turn order.
     *
     * @return a collection of players in the order they are seated/act.
     */
    public Collection<Player> getPlayers() {
        List<Player> ordered = new ArrayList<>(playerOrder.size());
        for (PlayerId id : playerOrder) {
            Player p = players.get(id);
            if (p != null) {
                ordered.add(p);
            }
        }
        return ordered;
    }

    /**
     * Returns the current player whose turn it is to act.
     *
     * @return the current player, or null if there are no players or the index is out of bounds.
     */
    public Player getCurrentPlayer() {
        PlayerId id = playerOrder.get(currentPlayerIndex);
        return players.get(id);
    }

    /**
     * Returns the index of the current player in the player order list.
     *
     * @return the index of the current player, or 0 if there are no players.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Returns whether a hand is currently active (i.e., in progress).
     *
     * @return true if a hand is active, false otherwise.
     */
    public boolean isHandActive() {
        return handActive;
    }

    /**
     * Returns the current phase of the game (e.g., PREFLOP, FLOP, TURN, RIVER).
     *
     * @return the current game phase.
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * Returns the current state of the table, including information such as the current bet and pot
     * size.
     *
     * @return the current table state.
     */
    public TableState getTableState() {
        return tableState;
    }

    /**
     * Returns the current pot, which includes the total amount of chips in the pot and the
     * contributions from each player.
     *
     * @return the current pot.
     */
    public Pot getPot() {
        return pot;
    }

    /**
     * Returns the current deck of cards being used in the game.
     *
     * @return the current deck of cards.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns the list of community cards currently on the table.
     *
     * @return a list of community cards, which may be empty if no cards have been dealt yet.
     */
    public List<Card> getCommunityCards() {
        return communityCards;
    }

    /**
     * Returns the hole cards for the specified player. If the player does not have hole cards yet,
     * an empty list is returned and stored in the map for future reference.
     *
     * @param playerId the ID of the player whose hole cards are being requested.
     * @return a list of hole cards for the specified player, which may be empty if the player has
     *     not been dealt cards yet.
     */
    public List<Card> getHoleCards(PlayerId playerId) {
        return holeCards.computeIfAbsent(playerId, k -> new ArrayList<>());
    }

    /**
     * Returns the index of the dealer in the player order list.
     *
     * @return the index of the dealer, or 0 if there are no players.
     */
    public int getDealerIndex() {
        return dealerIndex;
    }

    /**
     * Returns a map of player IDs to their respective hole cards. Each player's hole cards are
     * represented as a list of Card objects. If a player does not have hole cards yet, they will be
     * associated with an empty list.
     *
     * @return a map where the key is the player's ID and the value is a list of their hole cards.
     */
    public Map<PlayerId, List<Card>> getPlayerCards() {
        return holeCards;
    }

    /**
     * Returns the number of players currently in the game, based on the size of the player order
     * list.
     *
     * @return the number of players in the game.
     */
    public int getPlayerCount() {
        return players.size();
    }

    public void setCurrentPlayerIndex(int index) {
        if (playerOrder.isEmpty()) {
            this.currentPlayerIndex = 0;
            return;
        }
        this.currentPlayerIndex = index;
    }

    /**
     * Sets the current player index to the first player who should act preflop, which is determined
     * based on the dealer index and the number of players.
     */
    public void setCurrentPlayerToPreflopFirstToAct() {
        int size = playerOrder.size();
        if (size == 0) {
            currentPlayerIndex = 0;
            return;
        }

        // Heads-up: dealer (small blind) acts first preflop.
        int first = (size == 2) ? dealerIndex : (dealerIndex + DEALER_OFFSET) % size;
        currentPlayerIndex = first;
        resetRoundActions();
        if (!canPlayerAct(currentPlayerIndex)) {
            nextPlayer();
        }
    }

    /**
     * Sets the current player index to the first player who should act postflop, which is the
     * player immediately to the left of the dealer (i.e., dealerIndex + 1).
     */
    public void setCurrentPlayerToPostflopFirstToAct() {
        int size = playerOrder.size();
        if (size == 0) {
            currentPlayerIndex = 0;
            return;
        }

        int first = (dealerIndex + 1) % size;
        currentPlayerIndex = first;
        resetRoundActions();
        if (!canPlayerAct(currentPlayerIndex)) {
            nextPlayer();
        }
    }

    /**
     * Marks the specified player as having acted in the current round.
     *
     * @param playerId the ID of the player to mark as having acted this round.
     */
    public void markActedThisRound(PlayerId playerId) {
        if (playerId != null) {
            actedThisRound.add(playerId);
        }
    }

    /**
     * Checks if the specified player has already acted in the current round by checking if their ID
     * is present in the set of players who have acted this round.
     *
     * @param playerId the ID of the player to check for having acted this round.
     * @return true if the player has acted this round, false otherwise.
     */
    public boolean hasActedThisRound(PlayerId playerId) {
        return playerId != null && actedThisRound.contains(playerId);
    }

    /**
     * Resets the tracking of which players have acted this round by clearing the set of player IDs.
     */
    public void resetRoundActions() {
        actedThisRound.clear();
    }

    /**
     * Sets whether a hand is currently active (i.e., in progress).
     *
     * @param handActive a boolean value indicating whether a hand is active (true) or not (false).
     */
    public void setHandActive(boolean handActive) {
        this.handActive = handActive;
    }

    /**
     * Sets the current phase of the game (e.g., PREFLOP, FLOP, TURN, RIVER).
     *
     * @param phase the GamePhase to set as the current phase of the game.
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /**
     * Sets the index of the dealer in the player order list.
     *
     * @param dealerIndex the index to set as the dealer index, which should be within the bounds of
     *     the player order list if it is not empty.
     */
    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    /**
     * Sets the current deck of cards being used in the game.
     *
     * @param deck the Deck object to set as the current deck of cards for the game. This should be
     *     a valid Deck instance that can be used for dealing cards during the game.
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    /**
     * Adds a new player to the game with the specified ID and initial chip count.
     *
     * @param id the PlayerId of the new player to add to the game.
     * @param chips the initial number of chips the player has, which can be used for betting during
     *     the game.
     */
    public void addPlayer(PlayerId id, int chips) {
        Player player = new Player(id, chips);

        players.put(id, player);
        playerOrder.add(id);

        currentBets.put(id, 0);
        playerBetCommitments.put(id, 0);

        holeCards.computeIfAbsent(id, k -> new ArrayList<>());
    }

    /**
     * Renames a player id across all game-state structures.
     *
     * @param oldId existing player id
     * @param newId new player id
     * @return true if the rename was applied, false otherwise
     */
    public synchronized boolean renamePlayerId(PlayerId oldId, PlayerId newId) {
        if (oldId == null || newId == null) {
            return false;
        }
        if (!players.containsKey(oldId)) {
            return false;
        }
        if (oldId.equals(newId)) {
            return true;
        }
        if (players.containsKey(newId)) {
            return false;
        }

        Player player = players.remove(oldId);
        if (player == null) {
            return false;
        }
        player.setId(newId);
        players.put(newId, player);

        int idx = playerOrder.indexOf(oldId);
        if (idx >= 0) {
            playerOrder.set(idx, newId);
        }

        moveMapEntry(currentBets, oldId, newId, 0);
        moveMapEntry(playerBetCommitments, oldId, newId, 0);
        moveMapEntry(holeCards, oldId, newId, new ArrayList<>());
        return true;
    }

    /**
     * Helper method to move an entry in a map from an old key to a new key, with a fallback value
     * if the old key is not present.
     *
     * @param map the map in which to move the entry, where the key is a PlayerId and the value is
     *     of type T.
     * @param oldId the existing PlayerId key that is being renamed.
     * @param newId the new PlayerId key to which the entry should be moved.
     * @param fallback the value to use if the oldId is not present in the map.
     * @param <T> the type of the values in the map, which can be any type that is used for
     *     player-related data in the game state.
     */
    private <T> void moveMapEntry(
            Map<PlayerId, T> map, PlayerId oldId, PlayerId newId, T fallback) {
        T value = map.remove(oldId);
        map.put(newId, value != null ? value : fallback);
    }

    // BETTING

    /**
     * Returns the current bet amount for the specified player.
     *
     * @param playerId the ID of the player whose current bet is being requested.
     * @return the current bet amount for the specified player, or 0 if the player does not have a
     *     current bet.
     */
    public int getCurrentBet(PlayerId playerId) {
        return currentBets.getOrDefault(playerId, 0);
    }

    /**
     * Sets the current bet amount for the specified player.
     *
     * @param playerId the ID of the player whose current bet is being set.
     * @param amount the new bet amount to set for the specified player.
     */
    public void setCurrentBet(PlayerId playerId, int amount) {
        currentBets.put(playerId, amount);
    }

    /** Reset bets to 0 for ALL players (do not clear the map). */
    public void resetBets() {
        for (PlayerId id : playerOrder) {
            currentBets.put(id, 0);
        }
        tableState.setCurrentBet(0);
    }

    /**
     * Adds the specified amount to the pot.
     *
     * @param amount the amount to add to the pot.
     */
    public void addToPot(int amount) {
        pot.add(amount);
    }

    /**
     * Returns whether out-of-turn actions are allowed in the current game state.
     *
     * @return true if out-of-turn actions are allowed, false otherwise.
     */
    public boolean isAllowOutOfTurn() {
        return allowOutOfTurn;
    }

    public void setAllowOutOfTurn(boolean allowOutOfTurn) {
        this.allowOutOfTurn = allowOutOfTurn;
    }

    /**
     * Retrieves the Player object associated with the given PlayerId.
     *
     * @param id the PlayerId of the player to retrieve.
     * @return the Player object associated with the specified PlayerId, or a RuntimeException if
     *     the player is not found.
     */
    public Player getPlayer(PlayerId id) {
        Player player = players.get(id);
        if (player == null) {
            throw new RuntimeException("Player not found: " + id);
        }

        return player;
    }

    /**
     * Retrieves the current bet commitment for the specified player.
     *
     * @param playerId the ID of the player whose current bet commitment is being requested.
     * @return the current bet commitment for the specified player, or 0 if the player does not have
     *     a bet commitment.
     */
    public int getCurrentBetCommitment(PlayerId playerId) {
        return playerBetCommitments.getOrDefault(playerId, 0);
    }

    public void setCurrentBetCommitment(PlayerId playerId, int amount) {
        playerBetCommitments.put(playerId, amount);
    }

    // CARDS

    /**
     * Gives (overwrites) the two hole cards for the given player. Ensures stable list identity
     * (important if other code holds references).
     */
    public void giveHoleCards(PlayerId playerId, Card c1, Card c2) {
        List<Card> cards = holeCards.computeIfAbsent(playerId, k -> new ArrayList<>());
        cards.clear();
        cards.add(c1);
        cards.add(c2);
    }

    /**
     * Adds a community card to the list of community cards on the table.
     *
     * @param card the Card object representing the community card to be added to the table.
     */
    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    /** Resets the community cards by clearing the list of community cards on the table. */
    public void resetCommunityCards() {
        communityCards.clear();
    }

    /** Advances the current player index to the next player who can act. */
    public void nextPlayer() {
        if (playerOrder.isEmpty()) {
            currentPlayerIndex = 0;
            return;
        }

        int start = currentPlayerIndex;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerOrder.size();

            if (canPlayerAct(currentPlayerIndex)) {
                return;
            }
        } while (currentPlayerIndex != start);
    }

    /**
     * Checks if the player at the specified index in the player order list is able to take an
     * action.
     *
     * @param index the index of the player in the player order list to check for actionability.
     * @return true if the player at the specified index can act (i.e., is not folded and not
     *     all-in), false otherwise.
     */
    private boolean canPlayerAct(int index) {
        if (index < 0 || index >= playerOrder.size()) {
            return false;
        }

        PlayerId id = playerOrder.get(index);
        Player p = players.get(id);
        return p != null && !p.isFolded() && !p.isAllIn();
    }

    public void rotateDealer() {
        dealerIndex = (dealerIndex + 1) % playerOrder.size();
    }

    /**
     * Returns the Player object representing the current dealer based on the dealer index in the
     * player order list.
     *
     * @return the Player object representing the current dealer, or null if there are no players or
     *     the dealer index is out of bounds.
     */
    public Player getDealer() {
        PlayerId id = playerOrder.get(dealerIndex);
        return players.get(id);
    }

    // HAND RESET / LIFECYCLE

    /**
     * Starts a new hand by resetting the game state for the next round of poker.
     *
     * <p>This:
     *
     * <ul>
     *   <li>sets {@code phase=PREFLOP}
     *   <li>resets pot/bets/commitments
     *   <li>clears community + hole cards
     *   <li>creates &amp; shuffles a new deck
     * </ul>
     */
    public void startNewHand() {
        handActive = true;
        phase = GamePhase.PREFLOP;

        pot = new Pot();

        resetBets();
        playerBetCommitments.clear();

        resetCommunityCards();
        holeCards.clear();

        for (PlayerId id : playerOrder) {
            holeCards.put(id, new ArrayList<>());
        }

        for (Player player : players.values()) {
            player.setFolded(false);
        }

        deck = new Deck();
        deck.shuffle();

        setCurrentPlayerToPreflopFirstToAct();
    }

    public void foldPlayer(PlayerId playerId) {
        getPlayer(playerId).setFolded(true);
    }

    /**
     * Checks if the specified player has folded by retrieving their Player object and checking
     * their folded status.
     *
     * @param playerId the ID of the player to check for folded status.
     * @return true if the player has folded, false otherwise.
     */
    public boolean isFolded(PlayerId playerId) {
        return getPlayer(playerId).isFolded();
    }

    /**
     * Counts the number of players who have not folded.
     *
     * @return The number of non-folded players
     */
    public int countNonFoldedPlayers() {
        int count = 0;
        for (Player p : players.values()) {
            if (p != null && !p.isFolded()) {
                count++;
            }
        }
        return count;
    }
}
