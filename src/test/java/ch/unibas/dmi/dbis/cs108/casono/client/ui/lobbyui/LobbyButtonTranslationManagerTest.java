package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LobbyButtonTranslationManagerTest {
    LobbyButtonTranslationManager manager;

    @BeforeEach
    void setUp() {
        manager = new LobbyButtonTranslationManager();
        manager.getButtonIdToLobbyId().clear();
        manager.saveTranslation();
    }

    @Test
    void testAddLobbyButton() throws Exception {
        manager.addLobbyButton(1, 100);
        assertEquals(100, manager.getLobbyIdForButton(1));
    }

    @Test
    void testRemoveLobbyButton() throws Exception {
        manager.addLobbyButton(2, 200);
        manager.removeLobbyButton(2);
        assertNull(manager.getLobbyIdForButton(2));
    }

    @Test
    void testIsFull() throws Exception {
        for (int i = 1; i <= 8; i++) {
            manager.addLobbyButton(i, 100 + i);
        }
        assertTrue(manager.isFull());
        Exception ex = assertThrows(Exception.class, () -> manager.addLobbyButton(9, 109));
        assertEquals("Grid is full!", ex.getMessage());
    }

    @Test
    void testSaveAndLoadTranslation() throws Exception {
        manager.addLobbyButton(3, 300);
        manager.saveTranslation();
        manager.getButtonIdToLobbyId().clear();
        manager.loadTranslation();
        assertEquals(300, manager.getLobbyIdForButton(3));
    }

    @Test
    void testGetButtonIdToLobbyId() throws Exception {
        manager.addLobbyButton(4, 400);
        Map<Integer, Integer> map = manager.getButtonIdToLobbyId();
        assertTrue(map.containsKey(4));
        assertEquals(400, map.get(4));
    }
}
