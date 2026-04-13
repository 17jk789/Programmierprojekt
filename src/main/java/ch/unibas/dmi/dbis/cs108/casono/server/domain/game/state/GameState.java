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

    private final Map<PlayerId, List<Card>> holeCards = new HashMap<>();
    private final List<Card> communityCards = new ArrayList<>();

    private Deck deck;

    private static final int DEALER_OFFSET = 3;

    // Getters
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

    public Player getCurrentPlayer() {
        PlayerId id = playerOrder.get(currentPlayerIndex);
        return players.get(id);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isHandActive() {
        return handActive;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public TableState getTableState() {
        return tableState;
    }

    public Pot getPot() {
        return pot;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    /** Always returns a mutable list (never null). */
    public List<Card> getHoleCards(PlayerId playerId) {
        return holeCards.computeIfAbsent(playerId, k -> new ArrayList<>());
    }

    public int getDealerIndex() {
        return dealerIndex;
    }

    public Map<PlayerId, List<Card>> getPlayerCards() {
        return holeCards;
    }

    public int getPlayerCount() {
        return players.size();
    }

    // Setters / Mutators
    public void setCurrentPlayerIndex(int index) {
        if (playerOrder.isEmpty()) {
            this.currentPlayerIndex = 0;
            return;
        }
        this.currentPlayerIndex = index;
    }

    public void setCurrentPlayerToPreflopFirstToAct() {
        int size = playerOrder.size();
        if (size == 0) {
            currentPlayerIndex = 0;
            return;
        }

        // Heads-up: dealer (small blind) acts first preflop.
        int first = (size == 2) ? dealerIndex : (dealerIndex + DEALER_OFFSET) % size;
        currentPlayerIndex = first;
        if (!canPlayerAct(currentPlayerIndex)) {
            nextPlayer();
        }
    }

    public void setCurrentPlayerToPostflopFirstToAct() {
        int size = playerOrder.size();
        if (size == 0) {
            currentPlayerIndex = 0;
            return;
        }

        int first = (dealerIndex + 1) % size;
        currentPlayerIndex = first;
        if (!canPlayerAct(currentPlayerIndex)) {
            nextPlayer();
        }
    }

    public void setHandActive(boolean handActive) {
        this.handActive = handActive;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void setDealerIndex(int dealerIndex) {
        this.dealerIndex = dealerIndex;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void addPlayer(PlayerId id, int chips) {
        Player player = new Player(id, chips);

        players.put(id, player);
        playerOrder.add(id);

        currentBets.put(id, 0);
        playerBetCommitments.put(id, 0);

        holeCards.computeIfAbsent(id, k -> new ArrayList<>());
    }

    // Betting
    public int getCurrentBet(PlayerId playerId) {
        return currentBets.getOrDefault(playerId, 0);
    }

    public void setCurrentBet(PlayerId playerId, int amount) {
        currentBets.put(playerId, amount);
    }

    /**
     * Reset bets to 0 for ALL players (do not clear the map). Also resets table current bet to 0.
     */
    public void resetBets() {
        for (PlayerId id : playerOrder) {
            currentBets.put(id, 0);
        }
        tableState.setCurrentBet(0);
    }

    public void addToPot(int amount) {
        pot.add(amount);
    }

    public boolean isAllowOutOfTurn() {
        return allowOutOfTurn;
    }

    public void setAllowOutOfTurn(boolean allowOutOfTurn) {
        this.allowOutOfTurn = allowOutOfTurn;
    }

    // Player helpers
    public Player getPlayer(PlayerId id) {
        Player player = players.get(id);
        if (player == null) {
            throw new RuntimeException("Player not found: " + id);
        }

        return player;
    }

    // Bet commitments
    public int getCurrentBetCommitment(PlayerId playerId) {
        return playerBetCommitments.getOrDefault(playerId, 0);
    }

    public void setCurrentBetCommitment(PlayerId playerId, int amount) {
        playerBetCommitments.put(playerId, amount);
    }

    // Cards

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

    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    public void resetCommunityCards() {
        communityCards.clear();
    }

    // Turn / dealer management
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

    public Player getDealer() {
        PlayerId id = playerOrder.get(dealerIndex);
        return players.get(id);
    }

    // Hand reset / lifecycle

    /**
     * Starts a new hand by resetting the game state for the next round of poker.
     *
     * <p>This:
     *
     * <ul>
     *   <li>sets {@code phase=PREFLOP}
     *   <li>resets pot/bets/commitments
     *   <li>clears community + hole cards
     *   <li>creates & shuffles a new deck
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

    public boolean isFolded(PlayerId playerId) {
        return getPlayer(playerId).isFolded();
    }
}
