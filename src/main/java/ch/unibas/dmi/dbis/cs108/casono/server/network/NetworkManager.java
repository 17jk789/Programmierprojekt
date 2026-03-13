package ch.unibas.dmi.dbis.cs108.casono.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;

/**
 * Creates and manages the server socket. Accepts new incomming connections and creates sessions.
 */
public class NetworkManager implements Runnable {
    private Integer port;
    private Logger logger;
    private Thread thread;
    private Boolean running;
    private SessionManager sessionManager;
    private EventBus eventBus;

    public NetworkManager(Integer port, SessionManager sessionManager, EventBus eventBus) {
        this.port = port;
        this.logger = LogManager.getLogger(NetworkManager.class);
        this.thread = new Thread(this, "networkManager");
        this.running = true;
        this.sessionManager = sessionManager;
        this.eventBus = eventBus;
        this.eventBus.subscribe(DisconnectEvent.class, event -> clientDisconnected(event));
    }

    /* Starts the internal thread to accept new connections.
     */
    public void start() {
        logger.debug("Starting server at port " + port);
        thread.start();
    }

    public void clientDisconnected(DisconnectEvent event) {
        logger.info("Session " + event.sessionId().value() + " disconnected adhasghd");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                Session session = new Session(new TcpTransport(clientSocket), eventBus);
                sessionManager.addSession(session);
                session.start();
            }

        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
