package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
        String gameIdString="";
        if(lobbyId >= 0) {
            gameIdString=" GAME="+lobbyId;
        } else {
            gameIdString=" GAME='-1'";
        }
        return String.format(
                "TYPE=%s%s USER='%s' TARGET='%s' TIME='%s' TEXT='%s'",
                this.type.toString(),
                gameIdString,
                this.sender,
                this.target,
                this.timestamp,
                this.message);
    }

    /** Pattern, to analyze the response String with the given parameters */
    public static Pattern msgRex =
            Pattern.compile(
                    "TYPE=(?<type>\\w+) " + "(GAME=(?<game>\\w+) )?" +
                            "USER=(?<user>\\w+) " + "(TARGET=(?<target>\\w+) )?" +
                            "TIME=(?<time>[0-9:.]+) " + "TEXT='(?<text>([^']|\\')+)'");


    /**
     * Method to create a Message Object, from the information given by the String
     *
     * @param response - String that got sent as a response from the server
     * @return - New Message Object
     */
    /*
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
                        -1,
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "LOBBY":

                int gameId = getGameId(m.group("game"));
                return new Message(
                        ChatType.LOBBY,
                        gameId,
                        m.group("user"),
                        null,
                        m.group("time"),
                        m.group("text"));

            case "WHISPER":
                return new Message(
                        ChatType.WHISPER,
                        -1,
                        m.group("user"),
                        m.group("target"),
                        m.group("time"),
                        m.group("text"));

            default:
                throw new RuntimeException("Unknown message type " + typeString);
        }
    }
    */

    private static int getGameId(String gameIdStr) {
        int gameId = -1;
        if (gameIdStr != null) {
            gameId = Integer.parseInt(gameIdStr);
        }
        return gameId;
    }

    public static Message toMessageReqPars(List<RequestParameter> parameters) {
        String typeString = getParString(parameters, "TYPE");
        ChatType type = ChatType.valueOf(typeString);
        return switch (type) {
            case GLOBAL -> new Message(ChatType.GLOBAL,
                    -1,
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
                    Integer.parseInt(getParString(parameters, "GAME", "-1")),
                    getParString(parameters, "USER"),
                    getParString(parameters, "TARGET"),
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT")
            );
        };

    }
    private static @NonNull String getParString(List<RequestParameter> parameters, String keyString) {
        return getParString(parameters, keyString, null);
    }
    private static @NonNull String getParString(List<RequestParameter> parameters, String keyString, String defaultVal) {
        Optional<String> parOption = parameters.stream().filter((p) -> keyString.equals(p.key()))
                .findFirst()
                .map(RequestParameter::value);
        if(parOption.isEmpty()) {
            if (defaultVal == null) {
                throw new RuntimeException("No " + keyString + " found");
            } else {
                return defaultVal;
            }
        }
        return parOption.get();
    }

    public ResponseBody toResponse(ResponseBodyBuilder builder) {
        builder.param("TYPE", type.name());
        builder.param("GAME", lobbyId);
        builder.param("USER", this.sender);
        if (target != null) {
            builder.param("TARGET", target);
        }
        builder.param("TIME", this.timestamp);
        builder.param("TEXT", this.message);
        return builder.build();
    }
}
