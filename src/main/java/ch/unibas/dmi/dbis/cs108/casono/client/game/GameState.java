package ch.unibas.dmi.dbis.cs108.casono.client.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the poker game, including the phase, pot size, current bet,
 * dealer position, active player, community cards, and player information.
 */
public class GameState {
    public List<Player> players = new ArrayList<>();
    public List<Card> communityCards = new ArrayList<>();
    public List<String> highscoreEntries = new ArrayList<>();

    public String phase;
    public int pot;
    public int currentBet;
    public int dealer;
    public int activePlayer;
    public int winnerIndex = -1;
    public List<String> winnerNames = new ArrayList<>();
    public int potPerWinner;
}
