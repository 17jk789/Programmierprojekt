package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LobbyButtonGridManagerTest {
    LobbyButtonGridManager gridManager;
    LobbyButtonTranslationManager translationManager;
    GridPane gridPane;

    @BeforeEach
    void setUp() {
        gridPane = new GridPane();
        translationManager = new LobbyButtonTranslationManager();
        translationManager.getButtonIdToLobbyId().clear();
        translationManager.saveTranslation();
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
