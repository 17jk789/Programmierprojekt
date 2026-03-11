package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientServiceTest {

    Server server = null;
    private Thread serverThread;

    @BeforeEach
    public void setUp() {
        server = new Server(5000);
        serverThread = new Thread(()-> server.simpleListenLoop());
        serverThread.start();
    }
    @AfterEach
    public void tearDown() {
        serverThread.interrupt();
    }
    @Test
    public void testClientService() throws ExecutionException, InterruptedException {
        ClientService client = new ClientService("localhost", 5000);
        Future<ArrayList<String>> future = client.sendMessage(new Message("bla", "name"));
        future.get();
        client.closeSocket();
    }
}
