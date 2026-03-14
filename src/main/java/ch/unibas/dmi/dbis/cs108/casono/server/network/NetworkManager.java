package ch.unibas.dmi.dbis.cs108.casono.server.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Creates and manages the server socket. Accepts new incoming connections and creates sessions. */
public class NetworkManager implements Runnable {
    private Integer port;
    private Logger logger;
    private Thread thread;
    private Boolean running;
    private SessionManager sessionManager;
    private EventBus eventBus;

    /**
     * Creates a new NetworkManager with the given port, session manager, and event bus.
     *
     * @param port the port to listen on
     * @param sessionManager the session manager to use
     * @param eventBus the event bus for events
     */
    public NetworkManager(Integer port, SessionManager sessionManager, EventBus eventBus) {
        this.port = port;
        this.logger = LogManager.getLogger(NetworkManager.class);
        this.thread = new Thread(this, "networkManager");
        this.running = true;
        this.sessionManager = sessionManager;
        this.eventBus = eventBus;
        this.eventBus.subscribe(DisconnectEvent.class, event -> clientDisconnected(event));
    }

    /** Starts the internal thread to accept new connections. */
    public void start() {
        logger.debug("Starting server at port " + port);
        thread.start();
    }

    /**
     * Handles client disconnection events.
     *
     * @param event the disconnect event
     */
    public void clientDisconnected(DisconnectEvent event) {
        logger.info("Session " + event.sessionId().value() + " disconnected adhasghd");
    }

    /** Runs the network manager loop, accepting connections. */
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
