package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ChatControllerTest {

    ChatController chatController1;
    ChatController chatController2;

    String name1 = "testperson1";
    String name2 = "testperson1";

    @BeforeEach
    public void setup() {
        ChatClientTest receivingChatClient = new ChatClientTest();
        ChatClientTest sendingChatClient = new ChatClientTest(receivingChatClient);
        this.chatController1 = new ChatController(name1, sendingChatClient);
        this.chatController2 = new ChatController(name2, receivingChatClient);
    }

    @AfterEach
    public void teardown() {}

    @Test
    public void sendGlobalMessage() {
        ChatModel global = new ChatModel(ChatType.GLOBAL, name2, -1, null);
        chatController2.getChatModelMap().put(new ChatController.ChatKey(ChatType.GLOBAL), global);

        Message msg1 = new Message(ChatType.GLOBAL, -1, name1, null, "Hallo Welt");
        Message msg2 = new Message(ChatType.GLOBAL, -1, name1, null, "$$Hallo$$Welt$$");
        Message msg3 = new Message(ChatType.GLOBAL, -1, name1, null, "_*'=(&%$/§&%");
        Message msg4 = new Message(ChatType.LOBBY, -1, name1, null, "Guten Tag");
        Message msg5 = new Message(ChatType.LOBBY, -1, name1, null, "$/&)$)(&=)$=");
        Message msg6 = new Message(ChatType.GLOBAL, -1, name1, null, "NAME=ANDERER_NUTZER");
        Message msg7 = new Message(ChatType.GLOBAL, -1, name1, null, "________");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg3);
        messages.add(msg4);
        messages.add(msg5);
        messages.add(msg6);
        messages.add(msg7);

        chatController1.onSendToNetwork(msg1);
        chatController1.onSendToNetwork(msg2);
        chatController1.onSendToNetwork(msg3);
        chatController1.onSendToNetwork(msg4);
        chatController1.onSendToNetwork(msg5);
        chatController1.onSendToNetwork(msg6);
        chatController1.onSendToNetwork(msg7);

        chatController2.receiveMessage();

        assertEquals(5, global.messages.size());

        assertEquals(messages.get(0), global.messages.get(0));
        assertEquals(messages.get(1), global.messages.get(1));
        assertEquals(messages.get(2), global.messages.get(2));

        assertNotEquals(messages.get(3), global.messages.get(3));
        assertNotEquals(messages.get(4), global.messages.get(4));

        assertEquals(messages.get(5), global.messages.get(3));
        assertEquals(messages.get(6), global.messages.get(4));
    }

    @Test
    public void sendLobbyMessage() {
        ChatModel lobby = new ChatModel(ChatType.LOBBY, name2, 1, null);
        chatController2.getChatModelMap().put(new ChatController.ChatKey(ChatType.LOBBY), lobby);

        Message msg1 = new Message(ChatType.LOBBY, 1, name1, null, "Hallo Welt");
        Message msg2 = new Message(ChatType.LOBBY, 1, name1, null, "$$Hallo$$Welt$$");
        Message msg3 = new Message(ChatType.LOBBY, 1, name1, null, "_*'=(&%$/§&%");
        Message msg4 = new Message(ChatType.LOBBY, 4, name1, null, "Guten Tag");
        Message msg5 = new Message(ChatType.LOBBY, 5, name1, null, "$/&)$)(&=)$=");
        Message msg6 = new Message(ChatType.LOBBY, 1, name1, null, "NAME=ANDERER_NUTZER");
        Message msg7 = new Message(ChatType.LOBBY, 1, name1, null, "________");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg3);
        messages.add(msg4);
        messages.add(msg5);
        messages.add(msg6);
        messages.add(msg7);

        for (Message msg : messages) {
            chatController1.onSendToNetwork(msg);
        }

        chatController2.receiveMessage();

        assertEquals(5, lobby.messages.size());

        assertEquals(messages.get(0), lobby.messages.get(0));
        assertEquals(messages.get(1), lobby.messages.get(1));
        assertEquals(messages.get(2), lobby.messages.get(2));

        assertNotEquals(messages.get(3), lobby.messages.get(3));
        assertNotEquals(messages.get(4), lobby.messages.get(4));

        assertEquals(messages.get(5), lobby.messages.get(3));
        assertEquals(messages.get(6), lobby.messages.get(4));
    }

    @Test
    public void sendWhisperMessage() {
        ChatModel whisper = new ChatModel(ChatType.WHISPER, name2, -1, name1);
        chatController2.getChatModelMap().put(new ChatController.ChatKey(ChatType.WHISPER, name1), whisper);

        Message msg1 = new Message(ChatType.WHISPER, -1, name1, name2, "Hallo Welt");
        Message msg2 = new Message(ChatType.WHISPER, -1, name1, name2, "$$Hallo$$Welt$$");
        Message msg3 = new Message(ChatType.WHISPER, -1, name1, name2, "_*'=(&%$/§&%");
        Message msg4 = new Message(ChatType.WHISPER, -1, name1, name2, "NAME=ANDERER_NUTZER");
        Message msg5 = new Message(ChatType.WHISPER, -1, name1, name2, "________");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg3);
        messages.add(msg4);
        messages.add(msg5);

        for (Message msg : messages) {
            chatController1.onSendToNetwork(msg);
        }

        chatController2.receiveMessage();

        assertEquals(5, whisper.messages.size());

        assertEquals(messages.get(0), whisper.messages.get(0));
        assertEquals(messages.get(1), whisper.messages.get(1));
        assertEquals(messages.get(2), whisper.messages.get(2));

        assertEquals(messages.get(3), whisper.messages.get(3));
        assertEquals(messages.get(4), whisper.messages.get(4));
    }
}
