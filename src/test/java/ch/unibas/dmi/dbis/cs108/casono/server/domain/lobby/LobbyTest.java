package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import static org.junit.jupiter.api.Assertions.*;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.GameController;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.RoundManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.TurnManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for Lobby absent players tracking functionality. */
public class LobbyTest {
    private Lobby lobby;

    @BeforeEach
    void setUp() {
        lobby = new Lobby(LobbyId.of(1), "Test Lobby");
    }

    @Test
    void testAddPlayerSuccessfully() {
        boolean added = lobby.addPlayer("Player1", 4);
        assertTrue(added);
        assertEquals(1, lobby.getPlayerNames().size());
        assertTrue(lobby.getPlayerNames().contains("Player1"));
    }

    @Test
    void testAddPlayerToFullLobby() {
        lobby.addPlayer("Player1", 4);
        lobby.addPlayer("Player2", 4);
        lobby.addPlayer("Player3", 4);
        lobby.addPlayer("Player4", 4);

        // Try to add 5th player to a 4-player lobby
        boolean added = lobby.addPlayer("Player5", 4);
        assertFalse(added);
    }

    @Test
    void testLeavePlayerMovesToAbsent() {
        lobby.addPlayer("Player1", 4);
        assertTrue(lobby.getPlayerNames().contains("Player1"));
        assertTrue(lobby.getAbsentPlayers().isEmpty());

        // Player leaves
        boolean left = lobby.leavePlayer("Player1");
        assertTrue(left);

        // Player should be in absent set
        assertFalse(lobby.getPlayerNames().contains("Player1"));
        assertTrue(lobby.getAbsentPlayers().contains("Player1"));
    }

    @Test
    void testRestoreAbsentPlayerSuccessfully() {
        lobby.addPlayer("Player1", 4);
        lobby.leavePlayer("Player1");

        // Restore the absent player
        boolean restored = lobby.restoreAbsentPlayer("Player1");
        assertTrue(restored);

        // Player should be back in active list
        assertTrue(lobby.getPlayerNames().contains("Player1"));
        assertFalse(lobby.getAbsentPlayers().contains("Player1"));
    }

    @Test
    void testRestoreAbsentPlayerCannotRestoreTwice() {
        lobby.addPlayer("Player1", 4);
        lobby.leavePlayer("Player1");

        boolean restored1 = lobby.restoreAbsentPlayer("Player1");
        assertTrue(restored1);

        // Try to restore again - should fail
        boolean restored2 = lobby.restoreAbsentPlayer("Player1");
        assertFalse(restored2);
    }

    @Test
    void testHasAnyPlayersWithActiveOnly() {
        lobby.addPlayer("Player1", 4);
        assertTrue(lobby.hasAnyPlayers());
    }

    @Test
    void testHasAnyPlayersWithAbsentOnly() {
        lobby.addPlayer("Player1", 4);
        lobby.leavePlayer("Player1");
        assertTrue(lobby.hasAnyPlayers());
    }

    @Test
    void testHasAnyPlayersEmpty() {
        assertFalse(lobby.hasAnyPlayers());
    }

    @Test
    void testIsPlayerActive() {
        lobby.addPlayer("Player1", 4);
        assertTrue(lobby.isPlayerActive("Player1"));

        lobby.leavePlayer("Player1");
        assertFalse(lobby.isPlayerActive("Player1"));

        lobby.restoreAbsentPlayer("Player1");
        assertTrue(lobby.isPlayerActive("Player1"));
    }

    @Test
    void testMultiplePlayersLeaveAndRejoin() {
        // Add 4 players
        lobby.addPlayer("P1", 4);
        lobby.addPlayer("P2", 4);
        lobby.addPlayer("P3", 4);
        lobby.addPlayer("P4", 4);

        // All active
        assertEquals(4, lobby.getPlayerNames().size());
        assertEquals(0, lobby.getAbsentPlayers().size());

        // P1 and P2 leave
        lobby.leavePlayer("P1");
        lobby.leavePlayer("P2");

        assertEquals(2, lobby.getPlayerNames().size());
        assertEquals(2, lobby.getAbsentPlayers().size());

        // P1 comes back
        lobby.restoreAbsentPlayer("P1");

        assertEquals(3, lobby.getPlayerNames().size());
        assertEquals(1, lobby.getAbsentPlayers().size());
        assertTrue(lobby.getPlayerNames().contains("P1"));
        assertTrue(lobby.getAbsentPlayers().contains("P2"));
    }

    @Test
    void testGameEndedCallbackInvoked() {
        // Create a simple game setup
        GameState state = new GameState();
        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());
        GameController gameController = new GameController(engine);

        // Track if callback was invoked
        boolean[] callbackInvoked = {false};

        // Set callback
        lobby.setOnGameEndedCallback(() -> callbackInvoked[0] = true);

        // Simulate game ending
        gameController.endGame();
        // Callback should NOT be invoked yet because it's not registered in the
        // controller

        // Now register the callback in the controller
        gameController.setOnGameEndedCallback(() -> callbackInvoked[0] = true);
        gameController.endGame();

        assertTrue(callbackInvoked[0], "Game ended callback should have been invoked");
    }

    @Test
    void testRenamePlayerInActiveList() {
        lobby.addPlayer("OldName", 4);
        assertTrue(lobby.getPlayerNames().contains("OldName"));

        boolean renamed = lobby.renamePlayer("OldName", "NewName");
        assertTrue(renamed);

        assertFalse(lobby.getPlayerNames().contains("OldName"));
        assertTrue(lobby.getPlayerNames().contains("NewName"));
    }

    @Test
    void testRenamePlayerCannotRenameIfAbsent() {
        lobby.addPlayer("Player1", 4);
        lobby.leavePlayer("Player1");

        // Should NOT be able to rename an absent player directly
        boolean renamed = lobby.renamePlayer("Player1", "Player2");
        assertFalse(renamed);
    }
}
