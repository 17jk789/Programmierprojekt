package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.TestServer;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.*;

class LobbyButtonGridManagerTest {
    LobbyButtonGridManager gridManager;
    LobbyButtonTranslationManager translationManager;
    GridPane gridPane;
    TestServer testServer;

    @BeforeEach
    void setUp() throws Exception {
        gridPane = new GridPane();
        translationManager = LobbyButtonTranslationManager.getInstance();
        translationManager.getButtonIdToLobbyId().clear();
        // Start an in-process test server and connect a real ClientService to it.
        testServer = new TestServer();
        String host = "127.0.0.1";
        int port = testServer.getPort();
        ClientService clientService = new ClientService(host, port);
        gridManager = new LobbyButtonGridManager(gridPane, translationManager, clientService);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (testServer != null) {
            testServer.close();
        }
    }

    // @Test
    // void testCreateLobbyReturnsId() {
    //     int lobbyId = gridManager.createLobby();
    //     assertTrue(lobbyId > 0);
    // }
}
