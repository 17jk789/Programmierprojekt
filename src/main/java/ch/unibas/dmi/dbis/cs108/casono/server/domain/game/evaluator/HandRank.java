package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.evaluator;

import java.util.List;

/**
 * HandRank represents the rank of a poker hand, including its type (e.g., flush, straight) and the
 * kickers used for tie-breaking. It implements the Comparable interface to allow for easy
 * comparison between different hand ranks.
 */
public class HandRank implements Comparable<HandRank> {

    /**
     * The Type enumeration defines the different types of poker hands, each with an associated
     * strength value for comparison purposes.
     */
    public enum Type {
        HIGH_CARD(1),
        ONE_PAIR(2),
        TWO_PAIR(3),
        THREE_OF_A_KIND(4),
        STRAIGHT(5),
        FLUSH(6),
        FULL_HOUSE(7),
        FOUR_OF_A_KIND(8),
        STRAIGHT_FLUSH(9),
        ROYAL_FLUSH(10);

        private final int strength;

        Type(int strength) {
            this.strength = strength;
        }

        public int getStrength() {
            return strength;
        }
    }

    private final Type type;
    private final List<Integer> kickers;

    /**
     * Constructs a HandRank with the specified type and kickers.
     *
     * @param type The type of the hand (e.g., flush, straight).
     * @param kickers A list of integers representing the kickers for tie-breaking.
     */
    public HandRank(Type type, List<Integer> kickers) {
        this.type = type;
        this.kickers = kickers;
    }

    /**
     * Retrieves the type of the hand.
     *
     * @return The type of the hand (e.g., flush, straight).
     */
    public Type getType() {
        return type;
    }

    /**
     * Retrieves the list of kickers for tie-breaking.
     *
     * @return A list of integers representing the kickers for tie-breaking.
     */
    public List<Integer> getKickers() {
        return kickers;
    }

    /**
     * Compares this HandRank with another HandRank for ordering. The comparison is based first on
     * the type of the hand and then on the kickers for tie-breaking.
     *
     * @param other The other HandRank to compare against.
     * @return A negative integer, zero, or a positive integer as this HandRank is less than, equal
     *     to, or greater than the specified HandRank.
     */
    @Override
    public int compareTo(HandRank other) {

        int typeCompare = Integer.compare(this.type.getStrength(), other.type.getStrength());

        if (typeCompare != 0) {
            return typeCompare;
        }

        for (int i = 0; i < Math.min(kickers.size(), other.kickers.size()); i++) {

            int cmp = Integer.compare(kickers.get(i), other.kickers.get(i));

            if (cmp != 0) {
                return cmp;
            }
        }

        return 0;
    }
}
