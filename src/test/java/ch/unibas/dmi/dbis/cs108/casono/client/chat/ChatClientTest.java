package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChatClientTest implements ChatClientInterface {

    private ChatClientTest otherChatClient;

    public final Queue<Message> messageQueue;

    public ChatClientTest() {
        this.messageQueue = new LinkedList<>();
    }

    public ChatClientTest(ChatClientTest otherChatClient) {
        this.otherChatClient = otherChatClient;
        this.messageQueue = new LinkedList<>();
    }

    @Override
    public void sendMessage(Message message) {
        otherChatClient.messageQueue.add(message);
    }

    @Override
    public List<Message> getMessages() {
        List<Message> messages = new LinkedList<>();
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            messages.add(message);
        }
        return messages;
    }

    @Override
    public List<String> getUsers() {
        return List.of();
    }
}
