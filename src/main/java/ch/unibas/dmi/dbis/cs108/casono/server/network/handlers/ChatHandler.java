package ch.unibas.dmi.dbis.cs108.casono.server.network.handlers;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.ChatMessageEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;

import java.util.ArrayDeque;

public class ChatHandler {
    private final EventBus eventBus;
    private final ArrayDeque<Message> receivedMessages = new ArrayDeque<>();
    public ChatHandler(EventBus eventBus) {
        eventBus.subscribe(ChatMessageEvent.class, (event) -> {
            processMessageEvent(event.getMessage());
        });
        this.eventBus = eventBus;
    }

    private synchronized void processMessageEvent(Message message) {
        // TODO: filter
        this.receivedMessages.offer(message);
    }

    public synchronized int getMessageCount() {
        return this.receivedMessages.size();
    }
    public synchronized Message getNextMessage() {
        return this.receivedMessages.poll();
    }

    public void sendMessage(String messageString) {
        Message msg = Message.toMessage(messageString);
        this.eventBus.publish(new ChatMessageEvent(msg));
    }
}
