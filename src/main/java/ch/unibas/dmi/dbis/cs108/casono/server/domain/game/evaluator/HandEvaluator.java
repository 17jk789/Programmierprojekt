package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Card;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.deck.Suit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The HandEvaluator class provides functionality to evaluate a poker hand and determine its rank.
 * It analyzes a list of cards and identifies the best possible hand according to standard poker
 * rules, such as flushes, straights, pairs, and more. The evaluation process involves counting card
 * ranks, grouping by suits, and checking for specific hand combinations to assign the correct hand
 * rank.
 */
public class HandEvaluator {
    private static final int RANK_OFFSET = 2;
    private static final int MIN_FLUSH_SIZE = 5;
    private static final int ROYAL_FLUSH_ACE = 14;
    private static final int ROYAL_FLUSH_KING = 13;
    private static final int ROYAL_FLUSH_QUEEN = 12;
    private static final int ROYAL_FLUSH_JACK = 11;
    private static final int ROYAL_FLUSH_TEN = 10;
    private static final long FOUR_OF_A_KIND_COUNT = 4L;
    private static final int FOUR_OF_A_KIND = 4;
    private static final int THREE_OF_A_KIND = 3;
    private static final int PAIR = 2;
    private static final int FIRST_INDEX = 0;
    private static final int CARD_VALUE_OFFSET = 2;
    private static final int HAND_SIZE = 5;
    private static final long THREE_OF_A_KIND_COUNT = 3L;
    private static final int THREE_OF_A_KIND_RANK = 3;
    private static final int ONE_PAIR = 1;
    private static final int DEFAULT_VALUE = 0;
    private static final int STRAIGHT_LENGTH = 5;
    private static final int INITIAL_STREAK = 1;
    private static final int START_INDEX = 1;
    private static final int CARD_DIFFERENCE = 1;
    private static final int PREVIOUS_INDEX_OFFSET = -1;
    private static final int TWO_KICKERS = 2;
    private static final int ONE_KICKER = 1;
    private static final int THREE_KICKERS = 3;

    /**
     * Evaluates a list of cards and determines the best possible hand rank.
     *
     * @param cards A list of Card objects representing the player's hand.
     * @return A HandRank object representing the evaluated hand rank.
     */
    public static HandRank evaluate(List<Card> cards) {

        List<Integer> ranks = getSortedRanks(cards);
        Map<Integer, Long> rankCount = getRankCount(ranks);
        Map<Suit, List<Card>> suits = groupBySuit(cards);

        List<Card> flushCards = getFlushCards(suits);
        boolean flush = flushCards != null;
        boolean straight = isStraight(ranks);

        HandRank straightFlush = checkStraightFlush(flushCards);
        if (straightFlush != null) {
            return straightFlush;
        }

        HandRank fourKind = checkFourOfAKind(rankCount, ranks);
        if (fourKind != null) {
            return fourKind;
        }

        HandRank fullHouse = checkFullHouse(rankCount);
        if (fullHouse != null) {
            return fullHouse;
        }

        if (flush) {
            return buildFlush(flushCards);
        }

        if (straight) {
            return new HandRank(HandRank.Type.STRAIGHT, ranks);
        }

        HandRank threeKind = checkThreeOfAKind(rankCount, ranks);
        if (threeKind != null) {
            return threeKind;
        }

        HandRank twoPair = checkTwoPair(rankCount, ranks);
        if (twoPair != null) {
            return twoPair;
        }

        HandRank onePair = checkOnePair(rankCount, ranks);
        if (onePair != null) {
            return onePair;
        }

        return new HandRank(HandRank.Type.HIGH_CARD, ranks.stream().limit(HAND_SIZE).toList());
    }

    /**
     * Helper method to extract and sort the ranks of the cards in descending order.
     *
     * @param cards A list of Card objects representing the player's hand.
     * @return A sorted list of integer ranks corresponding to the cards.
     */
    private static List<Integer> getSortedRanks(List<Card> cards) {
        return cards.stream()
                .map(c -> c.getRank().ordinal() + RANK_OFFSET)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /**
     * Helper method to count the occurrences of each card rank in the hand.
     *
     * @param ranks A list of integer ranks representing the cards in the hand.
     * @return A map where the key is the card rank and the value is the count of occurrences.
     */
    private static Map<Integer, Long> getRankCount(List<Integer> ranks) {
        return ranks.stream().collect(Collectors.groupingBy(r -> r, Collectors.counting()));
    }

    /**
     * Helper method to group the cards by their suits.
     *
     * @param cards A list of Card objects representing the player's hand.
     * @return A map where the key is the Suit and the value is a list of Cards belonging to that
     *     suit.
     */
    private static Map<Suit, List<Card>> groupBySuit(List<Card> cards) {
        return cards.stream().collect(Collectors.groupingBy(Card::getSuit));
    }

    /**
     * Helper method to identify if there is a flush in the hand and return the corresponding cards.
     *
     * @param suits A map where the key is the Suit and the value is a list of Cards belonging to
     *     that suit.
     * @return A list of Card objects that form a flush, or null if no flush is found.
     */
    private static List<Card> getFlushCards(Map<Suit, List<Card>> suits) {
        return suits.values().stream()
                .filter(s -> s.size() >= MIN_FLUSH_SIZE)
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method to build a HandRank object for a flush hand.
     *
     * @param flushCards A list of Card objects that form a flush.
     * @return A HandRank object representing the flush hand rank.
     */
    private static HandRank buildFlush(List<Card> flushCards) {
        List<Integer> flushRanks =
                flushCards.stream()
                        .map(c -> c.getRank().ordinal() + CARD_VALUE_OFFSET)
                        .sorted(Comparator.reverseOrder())
                        .limit(HAND_SIZE)
                        .toList();

        return new HandRank(HandRank.Type.FLUSH, flushRanks);
    }

    /**
     * Helper method to check for a straight flush or royal flush in the hand.
     *
     * @param flushCards A list of Card objects that form a flush.
     * @return A HandRank object representing the straight flush or royal flush hand rank, or null
     *     if neither is found.
     */
    private static HandRank checkStraightFlush(List<Card> flushCards) {
        if (flushCards == null) {
            return null;
        }

        List<Integer> flushRanks =
                flushCards.stream()
                        .map(c -> c.getRank().ordinal() + RANK_OFFSET)
                        .sorted(Comparator.reverseOrder())
                        .toList();

        if (!isStraight(flushRanks)) {
            return null;
        }

        if (flushRanks.containsAll(
                List.of(
                        ROYAL_FLUSH_ACE,
                        ROYAL_FLUSH_KING,
                        ROYAL_FLUSH_QUEEN,
                        ROYAL_FLUSH_JACK,
                        ROYAL_FLUSH_TEN))) {

            return new HandRank(HandRank.Type.ROYAL_FLUSH, flushRanks);
        }

        return new HandRank(HandRank.Type.STRAIGHT_FLUSH, flushRanks);
    }

    /**
     * Helper method to check for a four of a kind hand rank. Stores the quad rank and 1 kicker for
     * tie-breaking.
     *
     * @param rankCount A map where the key is the card rank and the value is the count of
     *     occurrences.
     * @param allRanks A sorted list of all card ranks in the hand.
     * @return A HandRank object representing the four of a kind hand rank, or null if not found.
     */
    private static HandRank checkFourOfAKind(Map<Integer, Long> rankCount, List<Integer> allRanks) {
        if (!rankCount.containsValue(FOUR_OF_A_KIND_COUNT)) {
            return null;
        }

        int quad = getRank(rankCount, FOUR_OF_A_KIND);
        List<Integer> kickers = getKickers(allRanks, List.of(quad), ONE_KICKER);

        List<Integer> result = new ArrayList<>(List.of(quad));
        result.addAll(kickers);

        return new HandRank(HandRank.Type.FOUR_OF_A_KIND, result);
    }

    /**
     * Helper method to check for a full house hand rank.
     *
     * @param rankCount A map where the key is the card rank and the value is the count of
     *     occurrences.
     * @return A HandRank object representing the full house hand rank, or null if not found.
     */
    private static HandRank checkFullHouse(Map<Integer, Long> rankCount) {

        List<Integer> trips =
                rankCount.entrySet().stream()
                        .filter(e -> e.getValue() >= THREE_OF_A_KIND)
                        .map(Map.Entry::getKey)
                        .sorted(Comparator.reverseOrder())
                        .toList();

        List<Integer> pairs =
                rankCount.entrySet().stream()
                        .filter(e -> e.getValue() >= PAIR)
                        .map(Map.Entry::getKey)
                        .sorted(Comparator.reverseOrder())
                        .toList();

        if (trips.isEmpty()) {
            return null;
        }

        int three = trips.get(FIRST_INDEX);

        Integer pair = pairs.stream().filter(p -> p != three).findFirst().orElse(null);

        if (pair == null) {
            return null;
        }

        return new HandRank(HandRank.Type.FULL_HOUSE, List.of(three, pair));
    }

    /**
     * Helper method to check for a three of a kind hand rank. Stores the trips rank and 2 kickers
     * for tie-breaking.
     *
     * @param rankCount A map where the key is the card rank and the value is the count of
     *     occurrences.
     * @param allRanks A sorted list of all card ranks in the hand.
     * @return A HandRank object representing the three of a kind hand rank, or null if not found.
     */
    private static HandRank checkThreeOfAKind(
            Map<Integer, Long> rankCount, List<Integer> allRanks) {
        if (!rankCount.containsValue(THREE_OF_A_KIND_COUNT)) {
            return null;
        }

        int tripsRank = getRank(rankCount, THREE_OF_A_KIND_RANK);
        List<Integer> kickers = getKickers(allRanks, List.of(tripsRank), TWO_KICKERS);

        List<Integer> result = new ArrayList<>(List.of(tripsRank));
        result.addAll(kickers);

        return new HandRank(HandRank.Type.THREE_OF_A_KIND, result);
    }

    /**
     * Helper method to check for a two pair hand rank. Stores the two pair ranks and 1 kicker for
     * tie-breaking. The kicker is the highest remaining card that is not part of either pair.
     *
     * @param rankCount A map where the key is the card rank and the value is the count of
     *     occurrences.
     * @param allRanks A sorted list of all card ranks in the hand.
     * @return A HandRank object representing the two pair hand rank, or null if not found.
     */
    private static HandRank checkTwoPair(Map<Integer, Long> rankCount, List<Integer> allRanks) {

        long pairCount = rankCount.values().stream().filter(v -> v == PAIR).count();

        if (pairCount < PAIR) {
            return null;
        }

        List<Integer> pairRanks =
                rankCount.entrySet().stream()
                        .filter(e -> e.getValue() == PAIR)
                        .map(Map.Entry::getKey)
                        .sorted(Comparator.reverseOrder())
                        .limit(PAIR)
                        .toList();

        List<Integer> kickers = getKickers(allRanks, pairRanks, ONE_KICKER);

        List<Integer> result = new ArrayList<>(pairRanks);
        result.addAll(kickers);

        return new HandRank(HandRank.Type.TWO_PAIR, result);
    }

    /**
     * Helper method to check for a one pair hand rank. Stores the pair rank and 3 kickers for
     * tie-breaking.
     *
     * @param rankCount A map where the key is the card rank and the value is the count of
     *     occurrences.
     * @param allRanks A sorted list of all card ranks in the hand.
     * @return A HandRank object representing the one pair hand rank, or null if not found.
     */
    private static HandRank checkOnePair(Map<Integer, Long> rankCount, List<Integer> allRanks) {

        long pairCount = rankCount.values().stream().filter(v -> v == PAIR).count();

        if (pairCount != ONE_PAIR) {
            return null;
        }

        int pair = getRank(rankCount, PAIR);
        List<Integer> kickers = getKickers(allRanks, List.of(pair), THREE_KICKERS);

        List<Integer> result = new ArrayList<>(List.of(pair));
        result.addAll(kickers);

        return new HandRank(HandRank.Type.ONE_PAIR, result);
    }

    /**
     * Helper method to get the rank of a card based on the count of occurrences in the hand.
     *
     * @param map A map where the key is the card rank and the value is the count of occurrences.
     * @param count The specific count to look for (e.g., 2 for pairs, 3 for three of a kind).
     * @return The rank of the card that matches the specified count, or 0 if none found.
     */
    private static int getRank(Map<Integer, Long> map, int count) {

        return map.entrySet().stream()
                .filter(e -> e.getValue() == count)
                .map(Map.Entry::getKey)
                .max(Integer::compare)
                .orElse(DEFAULT_VALUE);
    }

    /**
     * Helper method to extract kickers from the list of all card ranks. Excludes the ranks already
     * used in the main hand combination.
     *
     * @param allRanks A sorted list of all card ranks in the hand.
     * @param excludeRanks A list of ranks to exclude (e.g., ranks used in pairs, trips, etc.).
     * @param numKickers The number of kickers to extract.
     * @return A list of kicker ranks, sorted in descending order.
     */
    private static List<Integer> getKickers(
            List<Integer> allRanks, List<Integer> excludeRanks, int numKickers) {

        Set<Integer> excludeSet = new HashSet<>(excludeRanks);

        return allRanks.stream()
                .filter(rank -> !excludeSet.contains(rank))
                .limit(numKickers)
                .toList();
    }

    /**
     * Helper method to determine if a list of card ranks forms a straight.
     *
     * @param ranks A list of integer ranks representing the cards in the hand.
     * @return True if the ranks form a straight, false otherwise.
     */
    private static boolean isStraight(List<Integer> ranks) {

        Set<Integer> unique = new HashSet<>(ranks);

        // Wheel Straight A-2-3-4-5
        if (unique.containsAll(
                List.of(
                        ROYAL_FLUSH_ACE,
                        ROYAL_FLUSH_KING,
                        ROYAL_FLUSH_QUEEN,
                        ROYAL_FLUSH_JACK,
                        ROYAL_FLUSH_TEN))) {
            return true;
        }

        List<Integer> sorted = unique.stream().sorted().collect(Collectors.toList());

        int streak = INITIAL_STREAK;

        for (int i = START_INDEX; i < sorted.size(); i++) {

            if (sorted.get(i) == sorted.get(i + PREVIOUS_INDEX_OFFSET) + CARD_DIFFERENCE) {

                streak++;

                if (streak >= STRAIGHT_LENGTH) {
                    return true;
                }

            } else {
                streak = INITIAL_STREAK;
            }
        }

        return false;
    }
}
