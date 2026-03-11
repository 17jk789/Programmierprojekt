package ch.unibas.dmi.dbis.cs108.casono.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.unibas.dmi.dbis.cs108.casono.common.network.Session;

public class NetworkManager implements Runnable {
    private Integer port;
    private Logger logger;
    private Thread thread;
    private Boolean running;

    public NetworkManager(Integer port) {
        this.port = port;
        this.logger = LogManager.getLogger(NetworkManager.class);
        this.thread = new Thread(this, "networkManager");
        this.running = true;
    }

    public void start() {
        logger.debug("Starting server at port " + port);
        thread.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                clientSocket.close();
            }

        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
