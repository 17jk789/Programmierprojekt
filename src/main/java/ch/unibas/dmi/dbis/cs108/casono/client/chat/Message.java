package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message Object for internal handling of Chat-Messages TODO: Should be used on both sides of the
 * network
 */
public class Message {
    private final ChatType type;
    private final String message;
    public String sender;
    public String timestamp;
    public int lobbyId = 0;
    public String target = null;

    /**
     * Constructor for creating Messages with all information given
     *
     * @param type - Either global, local or whisper
     * @param lobbyId - lobby id, or null, if the typChatType
     * @param sender - username
     * @param target - username of the target user, for whisper chat
     * @param message
     */
    public Message(
            ChatType type,
            int lobbyId,
            String sender,
            String target,
            String timestamp,
            String message) {
        this.type = type;
        this.lobbyId = lobbyId;
        this.sender = sender;
        this.target = target;
        this.timestamp = timestamp;
        this.message = message;
    }

    /**
     * Constructor for creating the Messages of the current user, using this client -> time of
     * writing is being recorded
     *
     * @param type - Either global, local or whisper
     * @param lobbyId - lobby id, or null, if the type is global
     * @param sender - username
     * @param target - username of the target user, for whisper chat
     * @param message
     */
    public Message(ChatType type, int lobbyId, String sender, String target, String message) {
        this.type = type;
        this.lobbyId = lobbyId;
        this.sender = sender;
        this.target = target;
        this.message = message;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.timestamp = now.format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public ChatType getMessageType() {
        return type;
    }

    /**
     * Method to create the request representation of the message object, to be sent to the server
     *
     * @return - request as specified in the network protocol, as String
     */
    public String toArgsString() {
        return String.format(
                "TYPE=%s GAME=%d USER=%s TARGET=%s TIME=%s TEXT='%s'",
                this.type.toString(),
                this.lobbyId,
                this.sender,
                this.target,
                this.timestamp,
                this.message);
    }

    /** Pattern, to analyze the response String with the given parameters */
    public static Pattern msgRex =
            Pattern.compile(
                    "TYPE=(?<type>\\w+) " + "GAME=(?<game>\\w+) " +
                            "USER=(?<user>\\w+) " + "TARGET=(?<target>\\w+) " +
                            "TIME=(?<time>[0-9:.]+) " + "TEXT='(?<text>([^']|\\')+)'");

    /**
     * Method to create a Message Object, from the information given by the String
     *
     * @param response - String that got sent as a response from the server
     * @return - New Message Object
     */
    public static Message toMessage(String response) {
        Matcher m = msgRex.matcher(response);
        if (!m.matches()) {
            throw new RuntimeException("Can not parse message: '" + response + "'");
        }
        String typeString = m.group("type");

        switch (typeString) {
            case "GLOBAL":
                return new Message(
                        ChatType.GLOBAL,
                        0,
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "LOBBY":
                return new Message(
                        ChatType.LOBBY,
                        Integer.parseInt(m.group("game")),
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "WHISPER":
                return new Message(
                        ChatType.WHISPER,
                        Integer.parseInt(m.group("game")),
                        m.group("user"),
                        m.group("target"),
                        m.group("time"),
                        m.group("text"));

            default:
                throw new RuntimeException("Unknown message type " + typeString);
        }
    }

    public static Message toMessage(List<RequestParameter> parameters) {
        String typeString = getParString(parameters, "TYPE");
        ChatType type = ChatType.valueOf(typeString);
        return switch (type) {
            case GLOBAL -> new Message(ChatType.GLOBAL,
                    0,
                    getParString(parameters, "USER"),
                    null,
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT")
            );
            case LOBBY -> new Message(ChatType.LOBBY,
                    Integer.parseInt(getParString(parameters, "GAME")),
                    getParString(parameters, "USER"),
                    null,
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT")
            );
            case WHISPER -> new Message(ChatType.WHISPER,
                    Integer.parseInt(getParString(parameters, "GAME")),
                    getParString(parameters, "USER"),
                    getParString(parameters, "TARGET"),
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT")
            );
        };

    }

    private static @NonNull String getParString(List<RequestParameter> parameters, String keyString) {
        return parameters.stream().filter((p) -> "TYPE".equals(p.key()))
                .findFirst()
                .map(RequestParameter::value)
                .orElseThrow(() -> new RuntimeException("No " + keyString + " found"));
    }
}
