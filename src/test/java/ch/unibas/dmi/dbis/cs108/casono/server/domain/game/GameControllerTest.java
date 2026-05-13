package ch.unibas.dmi.dbis.cs108.casono.server.domain.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Rank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Suit;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.RoundManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.TurnManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandEvaluator;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandRank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.showdown.CardsSpeakRule;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * The GameControllerTest class contains unit tests for the GameController class, which manages the
 * flow of a poker game. These tests simulate various game scenarios, including player actions, card
 * dealing, and hand evaluation, to ensure that the GameController behaves as expected under
 * different conditions.
 */
public class GameControllerTest {

    private static final Logger LOGGER = Logger.getLogger(GameControllerTest.class.getName());

    /**
     * Helper method to retrieve the current player's ID from the game state.
     *
     * @param game The GameController instance from which to retrieve the current player's ID.
     * @return The PlayerId of the current player in the game.
     */
    private static PlayerId currentPlayerId(GameController game) {
        return game.getState().getCurrentPlayer().getId();
    }

    /**
     * Helper method to simulate a call action for the current player in the game.
     *
     * @param game The GameController instance on which to perform the call action for the current
     *     player.
     */
    private static void callCurrent(GameController game) {
        game.playerCall(currentPlayerId(game));
    }

    /**
     * Helper method to simulate a fold action for the current player in the game.
     *
     * @param game The GameController instance on which to perform the fold action for the current
     *     player.
     */
    private static void foldCurrent(GameController game) {
        game.playerFold(currentPlayerId(game));
    }

    /**
     * Verifies that the preflop phase ends once all active (non-folded) players have completed
     * their required actions for the current betting round.
     */
    @Test
    public void testPreflopEndsAfterOneActionPerActivePlayer() {

        GameState state = new GameState();
        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);
        game.addPlayer(PlayerId.of("P1"), 10000);
        game.addPlayer(PlayerId.of("P2"), 10000);
        game.addPlayer(PlayerId.of("P3"), 10000);
        game.addPlayer(PlayerId.of("P4"), 10000);

        game.startGame();

        assertEquals(GamePhase.PREFLOP, game.getState().getPhase());
        assertEquals(0, game.getCommunityCards().size());

        callCurrent(game);
        callCurrent(game);
        callCurrent(game);

        assertEquals(GamePhase.PREFLOP, game.getState().getPhase());
        assertEquals(0, game.getCommunityCards().size());

        callCurrent(game);

        assertEquals(GamePhase.FLOP, game.getState().getPhase());
        assertEquals(3, game.getCommunityCards().size());

        callCurrent(game);
        callCurrent(game);
        callCurrent(game);
        callCurrent(game);

        assertEquals(GamePhase.TURN, game.getState().getPhase());
        assertEquals(4, game.getCommunityCards().size());

        callCurrent(game);
        callCurrent(game);
        callCurrent(game);
        callCurrent(game);

        assertEquals(GamePhase.RIVER, game.getState().getPhase());
        assertEquals(5, game.getCommunityCards().size());
    }

    /**
     * Ensures that during the preflop phase, folded players are excluded from progression checks
     * and do not block phase advancement.
     */
    @Test
    public void testPreflopCountsOnlyNonFoldedPlayersForProgress() {

        GameState state = new GameState();
        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);
        game.addPlayer(PlayerId.of("A"), 10000);
        game.addPlayer(PlayerId.of("B"), 10000);
        game.addPlayer(PlayerId.of("C"), 10000);
        game.addPlayer(PlayerId.of("D"), 10000);

        game.startGame();
        assertEquals(GamePhase.PREFLOP, game.getState().getPhase());

        callCurrent(game);
        foldCurrent(game);
        callCurrent(game);

        assertEquals(GamePhase.PREFLOP, game.getState().getPhase());

        callCurrent(game);

        assertEquals(GamePhase.FLOP, game.getState().getPhase());
        assertEquals(3, game.getCommunityCards().size());
    }

    /**
     * This test simulates a specific game scenario where Julian is expected to win with a Two Pair
     * hand. It sets up a fixed deck to ensure that the desired cards are dealt to the players and
     * verifies that the hand evaluation correctly identifies Julian as the winner.
     */
    @Test
    public void testFullPokerGameSimulation1() {

        GameState state = new GameState();

        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("Julian"), 20000);
        game.addPlayer(PlayerId.of("Mathis"), 20000);
        game.addPlayer(PlayerId.of("Jona"), 20000);
        game.addPlayer(PlayerId.of("Lars"), 20000);

        assertEquals(4, game.getState().getPlayers().size());

        game.startGame();

        Map<PlayerId, List<Card>> playerCards = game.getPlayerCards();

        assertEquals(4, playerCards.size());

        for (List<Card> cards : playerCards.values()) {
            assertEquals(2, cards.size());
        }

        Set<String> seenCards = new HashSet<>();

        for (List<Card> cards : playerCards.values()) {
            for (Card c : cards) {
                String key = c.getRank() + "-" + c.getSuit();
                assertFalse(seenCards.contains(key));
                seenCards.add(key);
            }
        }

        callCurrent(game);
        game.playerFold(PlayerId.of("Mathis"));
        callCurrent(game);
        game.playerRaise(PlayerId.of("Lars"), 1200);

        // New street model: once all non-folded players acted, preflop is complete.
        assertEquals(GamePhase.FLOP, game.getState().getPhase());

        List<Card> board = game.getCommunityCards();
        assertTrue(board.size() >= 3 && board.size() <= 5);

        for (Card c : game.getCommunityCards()) {

            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(seenCards.contains(key));
            seenCards.add(key);
        }

        assertTrue(game.getState().getPot().getAmount() > 0);

        Player mathis = game.getState().getPlayer(PlayerId.of("Mathis"));

        assertNotNull(mathis);

        assertTrue(game.getCommunityCards().size() >= 3);

        assertEquals(4, game.getState().getPlayers().size());

        LOGGER.info("Test 1 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card dealing, and hand
     * evaluation. It verifies that the game state is updated correctly throughout the game and that
     * the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation2() {

        GameState state = new GameState();

        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("Julian"), 20000);
        game.addPlayer(PlayerId.of("Mathis"), 20000);
        game.addPlayer(PlayerId.of("Jona"), 20000);
        game.addPlayer(PlayerId.of("Lars"), 20000);

        assertEquals(4, game.getState().getPlayers().size(), "There should be exactly 4 players");

        game.startGame();

        assertEquals(GamePhase.PREFLOP, game.getState().getPhase(), "The hand must start preflop");

        Map<PlayerId, List<Card>> playerCards = game.getPlayerCards();

        assertEquals(4, playerCards.size(), "All players must receive cards");

        for (Map.Entry<PlayerId, List<Card>> entry : playerCards.entrySet()) {

            assertEquals(
                    2,
                    entry.getValue().size(),
                    "Player " + entry.getKey() + " must have exactly 2 cards");
        }

        Set<String> seenCards = new HashSet<>();

        for (List<Card> cards : playerCards.values()) {
            for (Card c : cards) {
                String key = c.getRank() + "-" + c.getSuit();
                assertFalse(seenCards.contains(key), "Duplicate card found: " + key);
                seenCards.add(key);
            }
        }

        int potBefore = game.getState().getPot().getAmount();

        game.playerRaise(currentPlayerId(game), 1200);
        callCurrent(game);
        foldCurrent(game);
        callCurrent(game);

        System.out.println("Pot after actions: " + game.getState().getPot().getAmount());

        int potAfter = game.getState().getPot().getAmount();

        assertTrue(potAfter >= potBefore, "The pot must be larger after actions");

        assertEquals(3, game.getCommunityCards().size(), "The flop must have 3 cards");

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 4; i++) {
            callCurrent(game);
        }

        assertEquals(4, game.getCommunityCards().size(), "A turn must result in 4 cards");

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 5; i++) {
            callCurrent(game);
        }

        assertEquals(5, game.getCommunityCards().size(), "The river must consist of 5 cards");

        for (Card c : game.getCommunityCards()) {

            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(seenCards.contains(key), "Duplicate board card detected: " + key);

            seenCards.add(key);
        }

        assertTrue(seenCards.size() <= 52, "There can be a maximum of 52 cards");

        assertTrue(game.getState().getPot().getAmount() > 0, "The pot must contain chips");

        for (Player p : game.getState().getPlayers()) {

            assertTrue(p.getChips() >= 0, "A player may not have any negative chips: " + p.getId());
        }

        assertEquals(
                4, game.getState().getPlayers().size(), "The number of players must not change");

        for (Player p : game.getState().getPlayers()) {

            LOGGER.info(p.getId() + " | Chips: " + p.getChips());
        }

        LOGGER.info("Pot: " + game.getState().getPot().getAmount());
        LOGGER.info("Board: " + game.getCommunityCards());

        LOGGER.info("Test 2 was successfully completed");
    }

    /**
     * This test simulates a specific game scenario where Julian is expected to win with a Two Pair
     * hand. It sets up a fixed deck to ensure that the desired cards are dealt to the players and
     * verifies that the hand evaluation correctly identifies Julian as the winner.
     */
    @Test
    public void testFullPokerGameSimulation3() {

        GameState state = new GameState();

        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("Julian"), 20000);
        game.addPlayer(PlayerId.of("Mathis"), 20000);
        game.addPlayer(PlayerId.of("Jona"), 20000);
        game.addPlayer(PlayerId.of("Lars"), 20000);

        Deck deck = new Deck();
        List<Card> cards = new ArrayList<>();

        cards.add(new Card(Suit.CLUBS, Rank.SEVEN));
        cards.add(new Card(Suit.HEARTS, Rank.TWO));
        cards.add(new Card(Suit.CLUBS, Rank.FIVE));
        cards.add(new Card(Suit.SPADES, Rank.FIVE));
        cards.add(new Card(Suit.HEARTS, Rank.KING));

        cards.add(new Card(Suit.DIAMONDS, Rank.ACE));
        cards.add(new Card(Suit.SPADES, Rank.ACE));

        cards.add(new Card(Suit.CLUBS, Rank.NINE));
        cards.add(new Card(Suit.DIAMONDS, Rank.TEN));

        cards.add(new Card(Suit.CLUBS, Rank.SEVEN));
        cards.add(new Card(Suit.SPADES, Rank.TWO));

        cards.add(new Card(Suit.CLUBS, Rank.KING));
        cards.add(new Card(Suit.HEARTS, Rank.KING));

        deck.setCards(cards);
        state.setDeck(deck);

        game.startGame();

        game.playerCall(PlayerId.of("Julian"));
        game.playerFold(PlayerId.of("Mathis"));
        game.playerCall(PlayerId.of("Jona"));
        game.playerRaise(PlayerId.of("Lars"), 1200);

        assertEquals(GamePhase.FLOP, game.getState().getPhase());

        assertTrue(game.getCommunityCards().size() >= 3);

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 5; i++) {
            callCurrent(game);
        }

        assertEquals(5, game.getCommunityCards().size());

        List<Card> board = game.getCommunityCards();

        List<Card> julian = state.getHoleCards(PlayerId.of("Julian"));
        List<Card> lars = state.getHoleCards(PlayerId.of("Lars"));

        List<Card> julianCards = new ArrayList<>(board);
        julianCards.addAll(julian);

        List<Card> larsCards = new ArrayList<>(board);
        larsCards.addAll(lars);

        HandRank julianRank = HandEvaluator.evaluate(julianCards);
        HandRank larsRank = HandEvaluator.evaluate(larsCards);

        assertTrue(julianRank.compareTo(larsRank) > 0);

        LOGGER.info("Correct scenario: Julian wins with two pair.");
        LOGGER.info("Test 3 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card dealing, and hand
     * evaluation. It verifies that the game state is updated correctly throughout the game and that
     * the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation4() {

        GameState state = new GameState();

        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("Julian"), 5000);
        game.addPlayer(PlayerId.of("Mathis"), 5000);
        game.addPlayer(PlayerId.of("Jona"), 5000);
        game.addPlayer(PlayerId.of("Lars"), 5000);

        assertEquals(4, game.getState().getPlayers().size());

        game.startGame();

        Map<PlayerId, List<Card>> cards = game.getPlayerCards();

        assertEquals(4, cards.size());

        Set<String> seen = new HashSet<>();
        for (List<Card> list : cards.values()) {
            assertEquals(2, list.size());

            for (Card c : list) {
                String key = c.getRank() + "-" + c.getSuit();
                assertFalse(seen.contains(key), "Duplicate hole card detected: " + key);
                seen.add(key);
            }
        }

        int potBefore = game.getState().getPot().getAmount();

        game.playerRaise(PlayerId.of("Julian"), 300);
        game.playerCall(PlayerId.of("Mathis"));
        game.playerFold(PlayerId.of("Jona"));
        game.playerCall(PlayerId.of("Lars"));

        for (Player p : game.getState().getPlayers()) {
            assertTrue(p.getChips() >= 0, "Negative chips detected: " + p.getId());
        }

        assertTrue(game.getState().getPot().getAmount() >= potBefore);

        assertEquals(3, game.getCommunityCards().size());

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 4; i++) {
            callCurrent(game);
        }
        assertEquals(4, game.getCommunityCards().size());

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 5; i++) {
            callCurrent(game);
        }
        assertEquals(5, game.getCommunityCards().size());

        Set<String> boardSeen = new HashSet<>();

        for (Card c : game.getCommunityCards()) {
            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(boardSeen.contains(key), "Duplicate board card: " + key);
            assertFalse(seen.contains(key), "Board card already used in hole cards: " + key);

            boardSeen.add(key);
        }

        assertTrue(game.getState().getPot().getAmount() > 0, "Pot must not be zero");

        assertEquals(4, game.getState().getPlayers().size(), "Player count must remain stable");

        long activePlayers =
                game.getState().getPlayers().stream().filter(p -> p.getChips() > 0).count();

        assertTrue(activePlayers >= 1, "At least one player must still have chips");

        assertNotNull(game.getState().getPhase());

        LOGGER.info("Test 4 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card dealing, and hand
     * evaluation. It verifies that the game state is updated correctly throughout the game and that
     * the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation5() {

        GameState state = new GameState();

        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("ShortStack"), 500);
        game.addPlayer(PlayerId.of("MidStack"), 2000);
        game.addPlayer(PlayerId.of("BigStack"), 5000);
        game.addPlayer(PlayerId.of("Caller"), 2000);

        game.startGame();

        game.playerRaise(currentPlayerId(game), 1000);
        callCurrent(game);
        callCurrent(game);
        callCurrent(game);

        for (Player p : game.getState().getPlayers()) {
            assertTrue(p.getChips() >= 0, "Negative chips detected for " + p.getId());
        }

        assertTrue(game.getState().getPot().getAmount() > 0);

        game.playerRaise(currentPlayerId(game), 100);

        int pot = game.getState().getPot().getAmount();
        assertTrue(pot > 0, "Pot must remain valid after re-raises");

        foldCurrent(game);
        foldCurrent(game);

        long activePlayers =
                game.getState().getPlayers().stream().filter(p -> p.getChips() > 0).count();

        assertTrue(activePlayers >= 1, "At least one player must remain");

        assertTrue(game.getCommunityCards().size() >= 3);

        for (int i = 0; i < 10 && game.getCommunityCards().size() < 5; i++) {
            callCurrent(game);
        }

        assertEquals(5, game.getCommunityCards().size());

        Set<String> allCards = new HashSet<>();

        for (List<Card> hand : game.getPlayerCards().values()) {
            for (Card c : hand) {
                String key = c.getRank() + "-" + c.getSuit();
                assertFalse(allCards.contains(key), "Duplicate hole card: " + key);
                allCards.add(key);
            }
        }

        for (Card c : game.getCommunityCards()) {
            String key = c.getRank() + "-" + c.getSuit();
            assertFalse(allCards.contains(key), "Card reused from hole cards: " + key);
            allCards.add(key);
        }

        assertEquals(4, game.getState().getPlayers().size());
        assertNotNull(game.getState().getPhase());
        assertTrue(game.getState().getPot().getAmount() > 0);

        PlayerId winner = game.determineWinner();

        assertNotNull(winner, "Winner should not be null");

        assertTrue(game.getPlayerCards().containsKey(winner), "Winner must be a valid player");

        LOGGER.info("Winner determined: " + winner);

        LOGGER.info("Test 5 was successfully completed");
    }

    /**
     * This test simulates a scenario where two players have identical Two Pair hands at showdown.
     */
    @Test
    public void testSplitPotWithTrueIdenticalTwoPair() {

        GameState state = new GameState();

        state.addPlayer(PlayerId.of("PlayerA"), 5000);
        state.addPlayer(PlayerId.of("PlayerB"), 5000);

        state.getHoleCards(PlayerId.of("PlayerA")).add(new Card(Suit.CLUBS, Rank.TWO));
        state.getHoleCards(PlayerId.of("PlayerA")).add(new Card(Suit.DIAMONDS, Rank.THREE));
        state.getHoleCards(PlayerId.of("PlayerB")).add(new Card(Suit.HEARTS, Rank.FOUR));
        state.getHoleCards(PlayerId.of("PlayerB")).add(new Card(Suit.SPADES, Rank.FIVE));

        state.addCommunityCard(new Card(Suit.CLUBS, Rank.QUEEN));
        state.addCommunityCard(new Card(Suit.DIAMONDS, Rank.QUEEN));
        state.addCommunityCard(new Card(Suit.SPADES, Rank.EIGHT));
        state.addCommunityCard(new Card(Suit.HEARTS, Rank.EIGHT));
        state.addCommunityCard(new Card(Suit.CLUBS, Rank.ACE));

        List<Card> board = state.getCommunityCards();

        List<Card> handA = new ArrayList<>(board);
        handA.addAll(state.getHoleCards(PlayerId.of("PlayerA")));

        List<Card> handB = new ArrayList<>(board);
        handB.addAll(state.getHoleCards(PlayerId.of("PlayerB")));

        HandRank rankA = HandEvaluator.evaluate(handA);
        HandRank rankB = HandEvaluator.evaluate(handB);

        assertEquals(0, rankA.compareTo(rankB), "Both hands must be exactly the same");

        CardsSpeakRule showdown = new CardsSpeakRule();
        List<Player> winners = showdown.determineWinners(state);

        assertEquals(2, winners.size(), "There must be two winners");
        assertEquals("PlayerA", winners.get(0).getName());
        assertEquals("PlayerB", winners.get(1).getName());

        int chipsBeforeA =
                state.getPlayers().stream()
                        .filter(p -> p.getId().equals(PlayerId.of("PlayerA")))
                        .findFirst()
                        .orElseThrow()
                        .getChips();
        int chipsBeforeB =
                state.getPlayers().stream()
                        .filter(p -> p.getId().equals(PlayerId.of("PlayerB")))
                        .findFirst()
                        .orElseThrow()
                        .getChips();

        state.getPot().add(1000);
        showdown.awardPot(state);

        Player playerA =
                state.getPlayers().stream()
                        .filter(p -> p.getId().equals(PlayerId.of("PlayerA")))
                        .findFirst()
                        .orElseThrow();
        Player playerB =
                state.getPlayers().stream()
                        .filter(p -> p.getId().equals(PlayerId.of("PlayerB")))
                        .findFirst()
                        .orElseThrow();

        assertEquals(chipsBeforeA + 500, playerA.getChips(), "Player A must receive half the pot");
        assertEquals(chipsBeforeB + 500, playerB.getChips(), "Player B must receive half the pot");
        assertEquals(0, state.getPot().getAmount(), "The pot must be 0 after the payout");

        LOGGER.info("Passed the Split-Pot-Test");
    }
}
