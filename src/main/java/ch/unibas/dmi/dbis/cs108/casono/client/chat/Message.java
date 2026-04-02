package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message Object for internal handling of Chat-Messages
 * TODO: Should be used on both sides of the network
 */

public class Message {
    private final MessageType type;
    private final String message;
    public String user;
    public String timestamp;
    public int game_id = 0;
    public String target = null;
    public enum MessageType {
        GLOBAL, LOBBY, WHISPER
    };

    /**
     * Constructor for creating Messages with all information given
     * @param type - Either global, local or whisper
     * @param game_id - lobby id, or null, if the type is global
     * @param user - username
     * @param target - username of the target user, for whisper chat
     * @param message
     */

    public Message(MessageType type, int game_id, String user, String target, String timestamp, String message) {
        this.type = type;
        this.game_id = game_id;
        this.user = user;
        this.target = target;
        this.timestamp = timestamp;
        this.message = message;
    }

    /**
     * Constructor for creating the Messages of the current user, using this client -> time of writing is being recorded
     * @param type - Either global, local or whisper
     * @param game_id - lobby id, or null, if the type is global
     * @param user - username
     * @param target - username of the target user, for whisper chat
     * @param message
     */

    public Message(MessageType type, int game_id, String user, String target, String message) {
        this.type = type;
        this.game_id = game_id;
        this.user = user;
        this.target = target;
        this.message = message;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.timestamp = now.format(formatter);
    }


    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return type;
    }

    /*
     * Method to test the system
     * @return - String representation of the Message instance, as for example "player1: Hello World"

    public String toString() {
        return String.format("%s: %s", this.user, this.message);
    }
    */
    /**
     * Method to create the request representation of the message object, to be sent to the server
     * @return - request as specified in the network protocol, as String
     */
    public String toArgsString() {
        return String.format("TYPE=%s GAME=%d USER=%s TARGET=%s TIME=%s TEXT=%s",
                this.type.toString(), this.game_id, this.user, this.target, this.timestamp, this.message);
    }


    /**
     * Pattern, to analyze the response String with the given parameters
     */
    public static Pattern msgRex = Pattern.compile(
            "TYPE=(?<type>\\w+) GAME=(?<game>\\w+) USER=(?<user>\\w+) TARGET=(?<target>\\w+) TIME=(?<time>[0-9:.]+) TEXT=(?<text>.*)$");

    /**
     * Method to create a Message Object, from the information given by the String
     * @param response - String that got sent as a response from the server
     * @return - New Message Object
     */
    public static Message toMessage(String response) {
        Matcher m = msgRex.matcher(response);
        if (! m.matches()) {
            throw new RuntimeException("Can not parse message: '" + response+"'");
        }
        String typeString=m.group("type");

        switch (typeString) {
            case "GLOBAL":
                return new Message(MessageType.GLOBAL,
                        0,
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "LOBBY":
                return new Message(MessageType.LOBBY,
                        Integer.parseInt(m.group("game")),
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "WHISPER":
                return new Message(MessageType.WHISPER,
                        Integer.parseInt(m.group("game")),
                        m.group("user"),
                        m.group("target"),
                        m.group("time"),
                        m.group("text"));

            default:
                throw new RuntimeException("Unknown message type " + typeString);
        }
    }
}
