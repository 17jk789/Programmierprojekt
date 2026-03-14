package ch.unibas.dmi.dbis.cs108.casono.server;

import ch.unibas.dmi.dbis.cs108.casono.server.network.NetworkManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;

/**
 * Application class for starting the server.
 */
public class ServerApp {
    public static void start(String arg) {
        int port = Integer.parseInt(arg);
        System.out.println("You've selected the server. It will accept connections at port " + port);

        EventBus eventBus = new EventBus();
        SessionManager sessionManager = new SessionManager();
        eventBus.subscribe(DisconnectEvent.class, event -> sessionManager.removeSession(event.sessionId()));
        NetworkManager networkManager = new NetworkManager(port, sessionManager, eventBus);
        networkManager.start();
    }
}
