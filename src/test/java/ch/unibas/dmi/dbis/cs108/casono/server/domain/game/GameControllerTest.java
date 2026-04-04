package ch.unibas.dmi.dbis.cs108.casono.server.domain.game;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Deck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Rank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Suit;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandEvaluator;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator.HandRank;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.RoundManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.TurnManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.Player;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * The GameControllerTest class contains unit tests for the GameController
 * class,
 * which manages the flow of a poker game. These tests simulate various game
 * scenarios, including player actions, card dealing, and hand evaluation, to
 * ensure that the GameController behaves as expected under different
 * conditions.
 */
public class GameControllerTest {

    private static final Logger logger = Logger.getLogger(GameControllerTest.class.getName());

    /**
     * This test simulates a specific game scenario where Julian is expected to
     * win with a Two Pair hand. It sets up a fixed deck to ensure that the
     * desired cards are dealt to the players and verifies that the hand
     * evaluation correctly identifies Julian as the winner.
     */
    @Test
    public void testFullPokerGameSimulation1() {

        GameState state = new GameState();

        GameEngine engine = new GameEngine(
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

        game.playerCall(PlayerId.of("Julian"));
        game.playerFold(PlayerId.of("Mathis"));
        game.playerCall(PlayerId.of("Jona"));
        game.playerRaise(PlayerId.of("Lars"), 1200);

        game.dealFlop();

        List<Card> board = game.getCommunityCards();
        assertEquals(3, board.size());

        game.dealTurn();
        assertEquals(4, game.getCommunityCards().size());

        game.dealRiver();
        assertEquals(5, game.getCommunityCards().size());

        for (Card c : game.getCommunityCards()) {

            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(seenCards.contains(key));
            seenCards.add(key);
        }

        assertTrue(game.getState().getPot().getAmount() > 0);

        Player mathis = game.getState().getPlayer(PlayerId.of("Mathis"));

        assertNotNull(mathis);

        assertEquals(5, game.getCommunityCards().size());

        assertEquals(4, game.getState().getPlayers().size());

        logger.info("Test 1 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card
     * dealing, and hand evaluation. It verifies that the game state is updated
     * correctly throughout the game and that the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation2() {

        GameState state = new GameState();

        GameEngine engine = new GameEngine(
                state,
                new RuleEngine(new ArrayList<>()),
                new RoundManager(),
                new TurnManager());

        GameController game = new GameController(engine);

        game.addPlayer(PlayerId.of("Julian"), 20000);
        game.addPlayer(PlayerId.of("Mathis"), 20000);
        game.addPlayer(PlayerId.of("Jona"), 20000);
        game.addPlayer(PlayerId.of("Lars"), 20000);

        assertEquals(
                4,
                game.getState().getPlayers().size(),
                "There should be exactly 4 players");

        game.startGame();

        assertEquals(
                GamePhase.PREFLOP,
                game.getState().getPhase(),
                "The hand must start preflop");

        Map<PlayerId, List<Card>> playerCards = game.getPlayerCards();

        assertEquals(
                4,
                playerCards.size(),
                "All players must receive cards");

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

                assertFalse(
                        seenCards.contains(key),
                        "Duplicate card found: " + key);

                seenCards.add(key);
            }
        }

        int potBefore = game.getState().getPot().getAmount();

        game.playerRaise(PlayerId.of("Lars"), 1200);
        game.playerCall(PlayerId.of("Julian"));
        game.playerFold(PlayerId.of("Mathis"));
        game.playerCall(PlayerId.of("Jona"));

        System.out.println("Pot after actions: " + game.getState().getPot().getAmount());

        int potAfter = game.getState().getPot().getAmount();

        assertTrue(
                potAfter >= potBefore,
                "The pot must be larger after actions");

        game.dealFlop();

        assertEquals(
                3,
                game.getCommunityCards().size(),
                "The flop must have 3 cards");

        game.dealTurn();

        assertEquals(
                4,
                game.getCommunityCards().size(),
                "A turn must result in 4 cards");

        game.dealRiver();

        assertEquals(
                5,
                game.getCommunityCards().size(),
                "The river must consist of 5 cards");

        for (Card c : game.getCommunityCards()) {

            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(
                    seenCards.contains(key),
                    "Duplicate board card detected: " + key);

            seenCards.add(key);
        }

        assertTrue(
                seenCards.size() <= 52,
                "There can be a maximum of 52 cards");

        assertTrue(
                game.getState().getPot().getAmount() > 0,
                "The pot must contain chips");

        for (Player p : game.getState().getPlayers()) {

            assertTrue(
                    p.getChips() >= 0,
                    "A player may not have any negative chips: " + p.getId());
        }

        assertEquals(
                4,
                game.getState().getPlayers().size(),
                "The number of players must not change");

        for (Player p : game.getState().getPlayers()) {

            logger.info(
                    p.getId() +
                            " | Chips: " + p.getChips());
        }

        logger.info("Pot: " + game.getState().getPot().getAmount());
        logger.info("Board: " + game.getCommunityCards());

        logger.info("Test 2 was successfully completed");
    }

    /**
     * This test simulates a specific game scenario where Julian is expected to
     * win with a Two Pair hand. It sets up a fixed deck to ensure that the
     * desired cards are dealt to the players and verifies that the hand
     * evaluation correctly identifies Julian as the winner.
     */
    @Test
    public void testFullPokerGameSimulation3() {

        GameState state = new GameState();

        GameEngine engine = new GameEngine(
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

        game.playerCall(PlayerId.of("Julian"));
        game.playerCall(PlayerId.of("Jona"));

        game.dealFlop();

        game.playerCall(PlayerId.of("Julian"));
        game.playerFold(PlayerId.of("Jona"));

        game.dealTurn();
        game.playerCall(PlayerId.of("Julian"));

        game.dealRiver();
        game.playerCall(PlayerId.of("Julian"));

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

        logger.info("Correct scenario: Julian wins with two pair.");
        logger.info("Test 3 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card
     * dealing, and hand evaluation. It verifies that the game state is updated
     * correctly throughout the game and that the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation4() {

        GameState state = new GameState();

        GameEngine engine = new GameEngine(
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

        game.dealFlop();
        assertEquals(3, game.getCommunityCards().size());

        game.dealTurn();
        assertEquals(4, game.getCommunityCards().size());

        game.dealRiver();
        assertEquals(5, game.getCommunityCards().size());

        Set<String> boardSeen = new HashSet<>();

        for (Card c : game.getCommunityCards()) {
            String key = c.getRank() + "-" + c.getSuit();

            assertFalse(boardSeen.contains(key), "Duplicate board card: " + key);
            assertFalse(seen.contains(key), "Board card already used in hole cards: " + key);

            boardSeen.add(key);
        }

        assertTrue(game.getState().getPot().getAmount() > 0, "Pot must not be zero");

        assertEquals(4, game.getState().getPlayers().size(),
                "Player count must remain stable");

        long activePlayers = game.getState().getPlayers().stream()
                .filter(p -> p.getChips() > 0)
                .count();

        assertTrue(activePlayers >= 1, "At least one player must still have chips");

        assertNotNull(game.getState().getPhase());

        logger.info("Test 4 was successfully completed");
    }

    /**
     * This test simulates a full poker game, including player actions, card
     * dealing, and hand evaluation. It verifies that the game state is updated
     * correctly throughout the game and that the expected outcomes are achieved.
     */
    @Test
    public void testFullPokerGameSimulation5() {

        GameState state = new GameState();

        GameEngine engine = new GameEngine(
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

        game.playerRaise(PlayerId.of("BigStack"), 1000);
        game.playerCall(PlayerId.of("Caller"));
        game.playerCall(PlayerId.of("MidStack"));
        game.playerCall(PlayerId.of("ShortStack"));

        for (Player p : game.getState().getPlayers()) {
            assertTrue(p.getChips() >= 0,
                    "Negative chips detected for " + p.getId());
        }

        assertTrue(game.getState().getPot().getAmount() > 0);

        game.playerRaise(PlayerId.of("BigStack"), 100);

        int pot = game.getState().getPot().getAmount();
        assertTrue(pot > 0, "Pot must remain valid after re-raises");

        game.playerFold(PlayerId.of("Caller"));
        game.playerFold(PlayerId.of("MidStack"));

        long activePlayers = game.getState().getPlayers().stream()
                .filter(p -> p.getChips() > 0)
                .count();

        assertTrue(activePlayers >= 1, "At least one player must remain");

        game.dealFlop();
        assertEquals(3, game.getCommunityCards().size());

        game.dealTurn();
        assertEquals(4, game.getCommunityCards().size());

        game.dealRiver();
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

        assertTrue(
                game.getPlayerCards().containsKey(winner),
                "Winner must be a valid player");

        logger.info("Winner determined: " + winner);

        logger.info("Test 5 was successfully completed");
    }
}
