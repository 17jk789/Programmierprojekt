package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.util.List;

/**
 * Interface used to create test instances of the ChatClient
 */
public interface ChatClientInterface {

    List<Message> getMessages();

    void sendMessage(Message message);

    List<String> getUsers();

}
