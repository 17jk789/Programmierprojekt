package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The ChatClient class is responsible for sending messages to the server and
 * retrieving messages from the server. It uses the ClientService to send
 * commands and receive responses from the server.
 */
public class ChatClient {

    private final ClientService clientService;
    private final Logger logger;

    /**
     * Constructs a ChatClient with the given ClientService for communication.
     *
     * @param clientService The ClientService instance used to send commands and
     *                      receive responses from the server.
     */
    public ChatClient(ClientService clientService) {
        this.clientService = clientService;
        this.logger = LogManager.getLogger(ChatClient.class);
    }

    /**
     * Send a Message to the server by converting it to a string format and
     * sending a "SEND_MESSAGE" command with the message content as arguments.
     *
     * @param message The Message object to be sent to the server.
     */
    public void sendMessage(Message message) {
        String request = "SEND_MESSAGE " + message.toArgsString();
        logger.info("Writing to server: " + request);
        clientService.processCommand(request);
    }

    /**
     * Retrieve messages from the server by first sending a "GET_MESSAGE_COUNT"
     * command to determine how many messages are available and then sending
     * "GET_NEXT_MESSAGE" commands in a loop to retrieve each message. The
     * retrieved messages are parsed into Message objects and returned as a list.
     *
     * @return A list of Message objects representing the messages retrieved from
     *         the server.
     */
    public List<Message> getMessages() {
        logger.info("Asking server for new messages");
        List<RequestParameter> countStr = ClientService.convertToRequestParameters(clientService.processCommand("GET_MESSAGE_COUNT"));
        RequestParameter countRes = countStr.getFirst();
        if (!countRes.key().equals("COUNT")) {
            logger.error("Not the right response from server");
        }
        int count = Integer.parseInt(countRes.value());
        logger.info("Got " + count + " messages");
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<RequestParameter> msgRes = ClientService.convertToRequestParameters(clientService.processCommand("GET_NEXT_MESSAGE"));
            Message msg = Message.toMessageReqPars(msgRes);
            messages.add(msg);
        }
        return messages;
    }
}
