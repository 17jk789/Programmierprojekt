package ch.unibas.dmi.dbis.cs108.casono.server.domain.game;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.BlindAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.CallAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.FoldAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.action.RaiseAction;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandEvaluator;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandRank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GameController is responsible for managing the flow of the poker game. It interacts with the
 * GameEngine to process player actions and update the game state accordingly.
 */
public class GameController {

    private final GameEngine engine;

    private final List<PlayerId> players = new ArrayList<>();

    private int dealerIndex = 0;

    private static final int NEXT_PLAYER_OFFSET = 3;
    private static final int DEALER_OFFSET = 1;
    private static final int SMALL_BLIND_OFFSET = 1;
    private static final int BIG_BLIND_OFFSET = 2;
    private static final int SMALL_BLIND = 100;
    private static final int BIG_BLIND = 200;

    /**
     * Initializes the GameController with a reference to the GameEngine.
     *
     * @param engine The GameEngine instance that manages the game state and logic.
     */
    public GameController(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Adds a player to the game with the specified name and initial chip count.
     *
     * @param name The name of the player to add.
     * @param chips The initial number of chips the player has.
     */
    public void addPlayer(PlayerId name, int chips) {

        // PlayerId id = PlayerId.of(name);
        //
        // players.add(id);
        // engine.getState().addPlayer(id, chips);

        players.add(name);
        engine.getState().addPlayer(name, chips);
    }

    /**
     * Initializes a new hand by preparing the deck, setting the phase to PREFLOP, rotating the
     * dealer, dealing hole cards, posting blinds, and setting the first active player.
     */
    public void startGame() {

        if (engine.getState().getDeck() == null) {
            Deck deck = new Deck();
            deck.shuffle();
            engine.getState().setDeck(deck);
        }

        engine.getState().setHandActive(true);
        engine.getState().setPhase(GamePhase.PREFLOP);

        rotateDealer();
        dealHoleCards();
        postBlinds();

        int nextPlayerIndex = (dealerIndex + NEXT_PLAYER_OFFSET) % players.size();
        engine.getState().setCurrentPlayerIndex(nextPlayerIndex);
    }

    /** Rotates the dealer position to the next player in the list. */
    private void rotateDealer() {
        dealerIndex = (dealerIndex + DEALER_OFFSET) % players.size();
    }

    /**
     * Returns the player currently acting as dealer.
     *
     * @return The name of the current dealer.
     */
    public PlayerId getDealer() {
        return players.get(dealerIndex);
    }

    /**
     * Determines the small and big blind players relative to the dealer and submits the
     * corresponding blind actions to the engine.
     */
    public void postBlinds() {

        PlayerId smallBlind = players.get((dealerIndex + SMALL_BLIND_OFFSET) % players.size());
        PlayerId bigBlind = players.get((dealerIndex + BIG_BLIND_OFFSET) % players.size());

        engine.processAction(new BlindAction(smallBlind, SMALL_BLIND));
        engine.processAction(new BlindAction(bigBlind, BIG_BLIND));
    }

    /** Deals hole cards to each player from the deck. */
    public void dealHoleCards() {

        Deck deck = engine.getState().getDeck();

        for (PlayerId player : players) {

            Card c1 = deck.draw();
            Card c2 = deck.draw();

            engine.getState().giveHoleCards(player, c1, c2);
        }
    }

    /** Draws three cards from the deck and adds them as community cards (flop). */
    public void dealFlop() {

        Deck deck = engine.getState().getDeck();

        engine.getState().addCommunityCard(deck.draw());
        engine.getState().addCommunityCard(deck.draw());
        engine.getState().addCommunityCard(deck.draw());
    }

    /**
     * Deals the turn by drawing one community card from the deck and adding it to the game state.
     */
    public void dealTurn() {
        engine.getState().addCommunityCard(engine.getState().getDeck().draw());
    }

    /**
     * Deals the river by drawing one community card from the deck and adding it to the game state.
     */
    public void dealRiver() {
        engine.getState().addCommunityCard(engine.getState().getDeck().draw());
    }

    /**
     * Processes a player's fold action by sending a FoldAction to the GameEngine.
     *
     * @param playerId The ID of the player who is folding.
     */
    public void playerFold(PlayerId playerId) {
        // engine.processAction(new FoldAction(PlayerId.of(playerId)));
        engine.processAction(new FoldAction(playerId));
    }

    /**
     * Processes a player's call action by sending a CallAction to the GameEngine.
     *
     * @param playerId The ID of the player who is calling.
     */
    public void playerCall(PlayerId playerId) {
        // engine.processAction(new CallAction(PlayerId.of(playerId)));
        engine.processAction(new CallAction(playerId));
    }

    /**
     * Processes a player's raise action by sending a RaiseAction to the GameEngine.
     *
     * @param playerId The ID of the player who is raising.
     * @param amount The amount the player is raising.
     */
    public void playerRaise(PlayerId playerId, int amount) {
        // engine.processAction(new RaiseAction(PlayerId.of(playerId), amount));
        engine.processAction(new RaiseAction(playerId, amount));
    }

    /**
     * Retrieves the current game state from the GameEngine.
     *
     * @return The current GameState object representing the state of the game.
     */
    public GameState getState() {
        return engine.getState();
    }

    /**
     * Retrieves the list of community cards currently on the table.
     *
     * @return A list of Card objects representing the community cards.
     */
    public List<Card> getCommunityCards() {
        return engine.getState().getCommunityCards();
    }

    /**
     * Retrieves the hole cards for each player in the game.
     *
     * @return A map where the key is the player's name and the value is a list of Card objects
     *     representing the player's hole cards.
     */
    public Map<PlayerId, List<Card>> getPlayerCards() {
        return engine.getState().getPlayerCards();
    }

    /**
     * Determines the winner of the current hand by evaluating the best possible poker hand for each
     * active player.
     *
     * <p>The evaluation is based on the player's two hole cards combined with the five community
     * cards on the board.
     *
     * @return The ID of the winning player.
     */
    public PlayerId determineWinner() {

        List<Card> board = engine.getState().getCommunityCards();
        GameState state = engine.getState();

        PlayerId bestPlayer = null;
        HandRank bestRank = null;

        for (PlayerId player : players) {

            if (state.isFolded(player)) {
                continue;
            }

            List<Card> cards = new ArrayList<>(board);
            cards.addAll(state.getHoleCards(player));

            HandRank rank = HandEvaluator.evaluate(cards);

            if (bestRank == null || rank.compareTo(bestRank) > 0) {
                bestRank = rank;
                bestPlayer = player;
            }
        }

        return bestPlayer;
    }
}
