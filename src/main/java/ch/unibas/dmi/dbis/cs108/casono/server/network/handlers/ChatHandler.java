package ch.unibas.dmi.dbis.cs108.casono.server.network.handlers;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.ChatMessageEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;

import java.util.ArrayDeque;

public class ChatHandler {
    private final EventBus eventBus;
    private final ArrayDeque<Message> receivedMessages = new ArrayDeque<>();

    /**
     * Creates a ChatHandler for a Session
     * @param eventBus
     */
    public ChatHandler(EventBus eventBus) {
        eventBus.subscribe(ChatMessageEvent.class, (event) -> {
            processMessageEvent(event.getMessage());
        });
        this.eventBus = eventBus;
    }

    /**
     * Sends the Chat Message that the server received to the queue
     * @param message
     */

    private synchronized void processMessageEvent(Message message) {
        // TODO: filter for Messages that were sent by the Client
        this.receivedMessages.offer(message);
    }


    /**
     * Returns the current size of the queue (Used for Command GET_MESSAGE_COUNT)
     */
    public synchronized int getMessageCount() {
        return this.receivedMessages.size();
    }

    /**
     * Takes the next message out of the queue, to be sent to the Client
     */
    public synchronized Message getNextMessage() {
        return this.receivedMessages.poll();
    }

    /**
     * Message is converted into a String and sent to the Client
     */
    public void sendMessage(String messageString) {
        Message msg = Message.toMessage(messageString);
        this.eventBus.publish(new ChatMessageEvent(msg));
    }
}
