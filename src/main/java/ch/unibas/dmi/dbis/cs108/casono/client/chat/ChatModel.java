package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatViewController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

/**
 * ChatModel, stores the data for a specific chat
 *
 * <p>Holds the current state of a chat
 */
public class ChatModel {

    public ArrayList<Message> messages;

    private final ChatType chattype;

    /**
     * The person currently using this client
     */
    public final String username;

    /**
     * The person to send the message to
     * If the chat is a whisper chat
     */
    private final String target;

    private final IntegerProperty count;

    public int lobbyId;

    public ChatModel(ChatType chattype, String username, int lobbyId, String target) {
        this.messages = new ArrayList<Message>();
        this.chattype = chattype;
        this.username = username;
        this.count = new SimpleIntegerProperty(0);
        this.lobbyId = lobbyId;
        this.target = target;
    }

    public ChatType getChattype() {
        return chattype;
    }

    /**
     * method, used by the ChatViewController, to access all new messages, that are stored in the
     * ChatModel
     */
    public synchronized String viewNextMessage() {
        count.subtract(1);
        Message msg = messages.getLast();
        return String.format("[%s] %s: %s", msg.timestamp, msg.sender, msg.getMessage());
    }

    /**
     * Adds a new message method used by the ChatController
     *
     * @param msg
     */
    public synchronized void addMessage(Message msg) {
        messages.add(msg);
        count.add(1);
    }


    public void addListener(ChatViewController chatViewController) {
        count.addListener(
                (_count, _p, n) ->
                {
                    if(n.intValue() > 0) {
                        chatViewController.showMessage();
                    }
                });
    }

    public String getTarget() {
        return target;
    }
}
