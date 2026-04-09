package ch.unibas.dmi.dbis.cs108.casono.server.domain.message;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;

/** NOT USED * */
public class MessageManager {
    private final UserRegistry userRegistry;

    public MessageManager(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public void broadcast(Message message) {
        // userRegistry.getAllUsers().forEach(user -> user.enqueueMessage(message));
    }
}
