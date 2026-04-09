package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.util.ArrayList;

/**
 * ChatModel, stores the data for a specific chat
 *
 * Holds the current state of a chat
 */

public class ChatModel {

    public ArrayList<Message> messages;

    public ChatType chattype;

    public String username;

    public int count;

    public enum ChatType {
        GLOBAL,
        LOBBY,
        WHISPER
    }

    /**
     * Creates a new ChatModel, given a username of the client
     * @param chattype
     * @param username
     */

    public ChatModel(ChatType chattype, String username) {
        this.messages = new ArrayList<Message>();
        this.chattype = chattype;
        this.username = username;
        this.count = 0;
    }

    /**
     * method, used by the ChatViewController, to access all new messages, that are stored in the ChatModel
     */
    public synchronized String viewNextMessage() {
        count--;
        Message msg = messages.getLast();
        return String.format("[%s] %s: %s", msg.timestamp, msg.user, msg.getMessage());
    }

    /**
     * Adds a new message
     * method used by the ChatController
     * @param msg
     */
    public synchronized void addMessage(Message msg) {
        messages.add(msg);
        count++;
    }

    /**
     * method to send all current messages to the ChatViewController, if needed
     */
    public void addCompleteChat() {
        for (int i = 0; i < this.messages.size(); i++) {
            Message msg = this.messages.get(i);
        }
    }
}
