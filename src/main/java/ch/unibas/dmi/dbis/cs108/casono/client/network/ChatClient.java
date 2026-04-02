package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatClient {

    private ClientService clientService;

    public ChatClient(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * sends a message to the server
     * @param message
     */

    public void sendMessage(Message message) {
        String request = "SEND_MESSAGE " + message.toArgsString();
        clientService.processCommand(request);
    }

    /**
     * Send a Request to get the number of Messages currently in the Queue for the client.
     * Then proceeds, if needed, to get the messages by sending
     */

    public List<Message> getMessages() {
        String countStr = clientService.processCommand("GET_MESSAGE_COUNT");
        int count = Integer.parseInt(countStr);
        System.out.println("Got " + count + " messages");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String  message = clientService.processCommand("GET_NEXT_MESSAGE");
            if (message != null) {
                Message message1 = Message.toMessage(message);
                messages.add(message1);
            }
        }
        return messages;
    }
}
