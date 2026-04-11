package ch.unibas.dmi.dbis.cs108.casono.client.game;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the poker game, including the phase, pot
 * size, current bet, dealer position,
 * active player, community cards, and player information.
 */
public class GameState {
    public String phase;
    public int pot;
    public int currentBet;
    public int dealer;
    public int activePlayer;

    public List<Card> communityCards = new ArrayList<>();
    public List<Player> players = new ArrayList<>();
}
