package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.*;

class LobbyButtonGridManagerTest {
    LobbyButtonGridManager gridManager;
    LobbyButtonTranslationManager translationManager;
    GridPane gridPane;

    @BeforeEach
    void setUp() {
        gridPane = new GridPane();
        translationManager = new LobbyButtonTranslationManager();
        translationManager.getButtonIdToLobbyId().clear();
        gridManager = new LobbyButtonGridManager(gridPane, translationManager);
    }

    @Test
    void testCreateLobbyReturnsId() {
        int lobbyId = gridManager.createLobby();
        assertTrue(lobbyId > 0);
    }

    @Test
    void testJoinLobbyPlaceholder() {
        // check if exception is thrown
        assertDoesNotThrow(() -> gridManager.joinLobby(123));
    }
}
