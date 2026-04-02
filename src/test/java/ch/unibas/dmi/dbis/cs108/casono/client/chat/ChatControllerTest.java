package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import ch.unibas.dmi.dbis.cs108.casono.server.ServerApp;
import javafx.application.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatControllerTest {

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void teardown() {

    }

    @Test
    public void testChatController() {
        Client client = new Client("user1");
        client.startApplication();
    }
}
