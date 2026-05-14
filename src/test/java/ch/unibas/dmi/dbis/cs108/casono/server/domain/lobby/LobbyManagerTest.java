package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for LobbyManager leave/rejoin functionality. */
public class LobbyManagerTest {
    private LobbyManager lobbyManager;

    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager(4);
    }

    @Test
    void testLeavePlayerFromLobbyMarkingAbsent() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");
        assertNotNull(lobbyId);

        // Add a player
        boolean joined = lobbyManager.addPlayerToLobby("Player1", lobbyId);
        assertTrue(joined);

        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertTrue(lobby.getPlayerNames().contains("Player1"));
        assertTrue(lobby.getAbsentPlayers().isEmpty());

        // Mark player as absent
        boolean left = lobbyManager.leavePlayerFromLobby("Player1", lobbyId);
        assertTrue(left);

        // Player should be in absent set
        assertFalse(lobby.getPlayerNames().contains("Player1"));
        assertTrue(lobby.getAbsentPlayers().contains("Player1"));

        // Player should still be in the playerToLobby mapping
        Lobby retrievedLobby = lobbyManager.getLobbyByUsername("Player1");
        assertEquals(lobbyId, retrievedLobby.getId());
    }

    @Test
    void testAbsentPlayerCanRejoin() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");

        // Add players to fill the lobby almost
        lobbyManager.addPlayerToLobby("Player1", lobbyId);
        lobbyManager.addPlayerToLobby("Player2", lobbyId);
        lobbyManager.addPlayerToLobby("Player3", lobbyId);

        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertEquals(3, lobby.getPlayerNames().size());

        // Player1 leaves
        lobbyManager.leavePlayerFromLobby("Player1", lobbyId);
        assertEquals(2, lobby.getPlayerNames().size());
        assertEquals(1, lobby.getAbsentPlayers().size());

        // Player1 tries to rejoin - should succeed without error
        boolean rejoined = lobbyManager.addPlayerToLobby("Player1", lobbyId);
        assertTrue(rejoined);

        assertEquals(3, lobby.getPlayerNames().size());
        assertEquals(0, lobby.getAbsentPlayers().size());
        assertTrue(lobby.getPlayerNames().contains("Player1"));
    }

    @Test
    void testAbsentPlayerDoesNotCountAsFullLobby() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");

        // Fill lobby completely
        lobbyManager.addPlayerToLobby("P1", lobbyId);
        lobbyManager.addPlayerToLobby("P2", lobbyId);
        lobbyManager.addPlayerToLobby("P3", lobbyId);
        lobbyManager.addPlayerToLobby("P4", lobbyId);

        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertEquals(4, lobby.getPlayerNames().size());

        // P1 leaves
        lobbyManager.leavePlayerFromLobby("P1", lobbyId);
        assertEquals(3, lobby.getPlayerNames().size());
        assertEquals(1, lobby.getAbsentPlayers().size());

        // P1 can rejoin without "LOBBY_FULL" error
        boolean rejoined = lobbyManager.addPlayerToLobby("P1", lobbyId);
        assertTrue(rejoined);
    }

    @Test
    void testPlayerToLobbyMappingPreservedAfterLeave() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");
        lobbyManager.addPlayerToLobby("Player1", lobbyId);

        // Verify mapping exists
        Lobby beforeLeave = lobbyManager.getLobbyByUsername("Player1");
        assertEquals(lobbyId, beforeLeave.getId());

        // Player leaves
        lobbyManager.leavePlayerFromLobby("Player1", lobbyId);

        // Mapping should still exist for rejoin
        Lobby afterLeave = lobbyManager.getLobbyByUsername("Player1");
        assertEquals(lobbyId, afterLeave.getId());
    }

    @Test
    void testRemoveLobbyCleanupsBothActiveAndAbsentPlayers() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");

        lobbyManager.addPlayerToLobby("P1", lobbyId);
        lobbyManager.addPlayerToLobby("P2", lobbyId);
        lobbyManager.leavePlayerFromLobby("P1", lobbyId);

        // Verify both are in lobby
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertTrue(lobby.getPlayerNames().contains("P2"));
        assertTrue(lobby.getAbsentPlayers().contains("P1"));

        // Remove lobby
        lobbyManager.removeLobby(lobbyId);

        // Both players should be removed from playerToLobby
        assertNull(lobbyManager.getLobbyByUsername("P1"));
        assertNull(lobbyManager.getLobbyByUsername("P2"));
    }

    @Test
    void testLeaveNonExistentPlayerFromLobby() {
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");

        // Try to remove a player that was never added
        boolean left = lobbyManager.leavePlayerFromLobby("NonExistent", lobbyId);
        assertFalse(left);
    }

    @Test
    void testLeavePlayerFromNonExistentLobby() {
        LobbyId nonExistentId = LobbyId.of(999);

        // Try to remove from non-existent lobby
        boolean left = lobbyManager.leavePlayerFromLobby("Player1", nonExistentId);
        assertFalse(left);
    }

    @Test
    void testGameStartedWithAbsentPlayerTracking() {
        // This test verifies that when a game starts with 4 players,
        // the absent players tracking is initialized and works
        LobbyId lobbyId = lobbyManager.createNewLobby("Test Lobby");

        lobbyManager.addPlayerToLobby("P1", lobbyId);
        lobbyManager.addPlayerToLobby("P2", lobbyId);
        lobbyManager.addPlayerToLobby("P3", lobbyId);
        // Game should start when 4th player joins
        lobbyManager.addPlayerToLobby("P4", lobbyId);

        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertNotNull(lobby.getGameController(), "Game should be started");
        assertEquals(4, lobby.getPlayerNames().size());
        assertEquals(0, lobby.getAbsentPlayers().size());

        // P1 leaves during game
        boolean left = lobbyManager.leavePlayerFromLobby("P1", lobbyId);
        assertTrue(left);

        assertEquals(3, lobby.getPlayerNames().size());
        assertEquals(1, lobby.getAbsentPlayers().size());
    }
}
