package ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the betting state of the current hand, including the pot size,
 * the current highest bet, and the individual bets made by each player.
 * It keeps track of the total pot, the current bet amount, and the bets made
 * by each player. This class is essential for managing the betting rounds and
 * ensuring that all players' bets are accounted for during the game.
 */
public class BettingState {

    private int pot;
    private int currentBet;

    private Map<String, Integer> playerBets = new HashMap<>();
}
