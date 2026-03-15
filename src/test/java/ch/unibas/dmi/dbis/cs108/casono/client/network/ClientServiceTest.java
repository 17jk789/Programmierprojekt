package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.Server;
import ch.unibas.dmi.dbis.cs108.casono.server.ServerApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServiceTest {

    Server server = null;
    private Thread serverThread;

    @BeforeEach
    public void setUp() {
        //server = new Server(5000);
        //serverThread = new Thread(()-> server.simpleListenLoop());
        //serverThread.start();
        ServerApp.startServerCore(5000);
    }
    @AfterEach
    public void tearDown() {

        //serverThread.interrupt();
    }

    @Test
    public void testClientService() throws ExecutionException, InterruptedException {
        ClientService client1 = new ClientService("localhost", 5000);
        ClientService client2 = new ClientService("localhost", 5000);
        Thread.sleep(2000);
        String response = client1.sendMessage(new Message(Message.MessageType.GLOBAL, 0, "Mathis", null, "blahh"));
        Thread.sleep(5000);
        List<Message> received = client2.getMessages();

        assertEquals(1, received.size());
        Message s = received.get(0);
        assertEquals(Message.MessageType.GLOBAL, s.getMessageType());
    }

    @Test
    public void testMessage() {
        Message msg = new Message(Message.MessageType.GLOBAL, 0, "Mathis", null, "blah");
        Message msg2 = new Message(Message.MessageType.WHISPER, 1, "Mathis", "Julian", "blah");
        String serial = msg.toArgsString();
        Message roundTripped = Message.toMessage(serial);
        String serial2 = msg2.toArgsString();
        Message roundTripped2 = Message.toMessage(serial2);

        assert(roundTripped.game_id == msg.game_id);
        assertEquals(0, roundTripped.game_id);
        assert(roundTripped2.game_id == msg2.game_id);
    }
}
