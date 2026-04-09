package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Message Object for internal handling of Chat-Messages
 */
public class Message {
    private final ChatType type;
    private final String message;
    public String sender;
    public String timestamp;
    public int lobbyId = 0;
    public String target = null;

    /**
     * Constructs a Message with a provided timestamp. Typically used when
     * reconstructing messages received from the server.
     *
     * @param type      The chat category (e.g., GLOBAL, LOBBY, or WHISPER).
     * @param lobbyId   The ID of the lobby, or -1 if not applicable.
     * @param sender    The username of the message creator.
     * @param target    The username of the recipient (required for whispers, otherwise null).
     * @param timestamp The formatted time string (e.g., "HH:mm").
     * @param message   The actual text content of the message.
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
     * Constructs a new Message for the current user. Automatically generates
     * a timestamp based on the local system time ("HH:mm").
     *
     * @param type    The chat category (e.g., GLOBAL, LOBBY, or WHISPER).
     * @param lobbyId The ID of the lobby, or -1 if not applicable.
     * @param sender  The username of the current user.
     * @param target  The username of the recipient (for whispers).
     * @param message The actual text content to be sent.
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

    /**
     * Returns the text content of the message.
     *
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the type of chat this message belongs to.
     *
     * @return The {@link ChatType}.
     */
    public ChatType getMessageType() {
        return type;
    }

    /**
     * Formats the message object into a string representation compatible with
     * the network protocol arguments.
     *
     * @return A formatted string containing all message attributes for server transmission.
     */
    public String toArgsString() {
        String gameIdString = "";
        if (lobbyId >= 0) {
            gameIdString = " GAME=" + lobbyId;
        } else {
            gameIdString = " GAME='-1'";
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

    /**
     * Parses a list of network request parameters to reconstruct a Message object.
     * Handles different chat types (GLOBAL, LOBBY, WHISPER) and their specific requirements.
     *
     * @param parameters A list of {@link RequestParameter} received from the network.
     * @return A new {@link Message} instance populated with the parsed data.
     */
    public static Message toMessageReqPars(List<RequestParameter> parameters) {
        String typeString = getParString(parameters, "TYPE");
        ChatType type = ChatType.valueOf(typeString);
        return switch (type) {
            case GLOBAL -> new Message(
                    ChatType.GLOBAL,
                    -1,
                    getParString(parameters, "USER"),
                    null,
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT"));
            case LOBBY -> new Message(
                    ChatType.LOBBY,
                    Integer.parseInt(getParString(parameters, "GAME")),
                    getParString(parameters, "USER"),
                    null,
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT"));
            case WHISPER -> new Message(
                    ChatType.WHISPER,
                    Integer.parseInt(getParString(parameters, "GAME", "-1")),
                    getParString(parameters, "USER"),
                    getParString(parameters, "TARGET"),
                    getParString(parameters, "TIME"),
                    getParString(parameters, "TEXT"));
        };
    }

    /**
     * Helper method to extract a specific parameter value by its key.
     *
     * @param parameters The list of parameters to search.
     * @param keyString The key to look for.
     * @return The value associated with the key.
     * @throws RuntimeException if the key is not found.
     */
    private static @NonNull String getParString(
            List<RequestParameter> parameters, String keyString) {
        return getParString(parameters, keyString, null);
    }

    /**
     * Helper method to extract a specific parameter value by its key, with a fallback default value.
     *
     * @param parameters The list of parameters to search.
     * @param keyString The key to look for.
     * @param defaultVal The value to return if the key is missing.
     * @return The found value or the default value.
     */
    private static @NonNull String getParString(
            List<RequestParameter> parameters, String keyString, String defaultVal) {
        Optional<String> parOption =
                parameters.stream()
                        .filter((p) -> keyString.equals(p.key()))
                        .findFirst()
                        .map(RequestParameter::value);
        if (parOption.isEmpty()) {
            if (defaultVal == null) {
                throw new RuntimeException("No " + keyString + " found");
            } else {
                return defaultVal;
            }
        }
        return parOption.get();
    }

    /**
     * Converts the message object into a network response body using the provided builder.
     *
     * @param builder The {@link ResponseBodyBuilder} used to construct the response.
     * @return The built {@link ResponseBody} containing the message data.
     */
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
