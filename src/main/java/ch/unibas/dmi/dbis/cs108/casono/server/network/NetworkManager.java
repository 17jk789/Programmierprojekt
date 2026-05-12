package ch.unibas.dmi.dbis.cs108.casono.server.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandRouter;
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
    private CommandRouter router;

    /**
     * Creates a new NetworkManager with the given port, session manager, and event bus.
     *
     * @param port the port to listen on
     * @param sessionManager the session manager to use
     */
    public NetworkManager(Integer port, SessionManager sessionManager, CommandRouter router) {
        this.port = port;
        this.logger = LogManager.getLogger(NetworkManager.class.getSimpleName());
        this.thread = new Thread(this, "networkManager");
        this.running = true;
        this.sessionManager = sessionManager;
        this.router = router;
    }

    /** Starts the internal thread to accept new connections. */
    public void start() {
        logger.debug("Starting at port {}", port);
        thread.start();
    }

    /** Runs the network manager loop, accepting connections. */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();

                logger.debug("Accepted connection from {}", clientSocket.getRemoteSocketAddress());

                sessionManager.create(new TcpTransport(clientSocket), router);
            }

        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
