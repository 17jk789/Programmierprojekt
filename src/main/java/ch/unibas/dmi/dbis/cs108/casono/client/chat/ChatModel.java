package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * ChatModel, stores the data for a specific chat
 *
 * <p>Holds the current state of a chat
 */
public class ChatModel {

    private ArrayList<Consumer<Message>> listeners = new ArrayList<>();

    public ArrayList<Message> messages;

    private final ChatType chattype;

    /** The person currently using this client */
    public final String username;

    /** The person to send the message to If the chat is a whisper chat */
    private final String target;

    private final IntegerProperty count;

    public int lobbyId;

    /**
     * Constructs a new ChatModel for a specific chat type.
     *
     * @param chattype The type of chat (e.g., GLOBAL, LOBBY, or WHISPER).
     * @param username The username of the current user.
     * @param lobbyId The ID of the lobby, or -1 if not applicable.
     * @param target The username of the whisper recipient, or null for other chat types.
     */
    public ChatModel(ChatType chattype, String username, int lobbyId, String target) {
        this.messages = new ArrayList<Message>();
        this.chattype = chattype;
        this.username = username;
        this.count = new SimpleIntegerProperty(0);
        this.lobbyId = lobbyId;
        this.target = target;
    }

    /**
     * Returns the type of chat this model represents.
     *
     * @return The {@link ChatType}.
     */
    public ChatType getChattype() {
        return chattype;
    }

    /**
     * Adds a new message to the history and notifies all registered listeners. This method is
     * synchronized to ensure thread safety when updating the message list.
     *
     * @param msg The {@link Message} to be added.
     */
    public synchronized void addMessage(Message msg) {
        messages.add(msg);
        listeners.stream().forEach((l) -> l.accept(messages.getLast()));
    }

    /**
     * Registers a listener to be notified whenever a new message is added to this model.
     *
     * @param listener A {@link Consumer} that processes the new {@link Message}.
     */
    public void addListener(Consumer<Message> listener) {
        this.listeners.add(listener);
    }

    /**
     * Returns the target user for this chat, primarily used for whispers.
     *
     * @return The target username or null.
     */
    public String getTarget() {
        return target;
    }
}
