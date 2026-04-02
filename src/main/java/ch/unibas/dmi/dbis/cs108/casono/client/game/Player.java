package ch.unibas.dmi.dbis.cs108.casono.client.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the poker game, including their name, chip count,
 * current bet, state (e.g., "active", "folded"), and their hole cards.
 */
public class Player {
    public String name;
    public int chips;
    public int bet;
    public String state;

    public List<Card> cards = new ArrayList<>();
}
