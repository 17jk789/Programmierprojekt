package ch.unibas.dmi.dbis.cs108.casono.server.network.events;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;

public class ChatMessageEvent implements Event {
    private final Message message;

    public ChatMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
