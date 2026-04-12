package ch.unibas.dmi.dbis.cs108.casono.client;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LoginResult;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point and bootstrap helper for the Casono client application.
 *
 * <p>This class is responsible for two tasks: - performing an optional startup LOGIN when a
 * username is supplied on the command line, and - providing a shared {@link ClientService} instance
 * that the UI can reuse so the initial connection remains active.
 */
public class ClientApp {

    private static final Logger LOGGER = LogManager.getLogger(ClientApp.class);

    /** Shared client connection used when a username is provided at startup. */
    private static volatile ClientService sharedClientService;

    /** Default constructor. */
    public ClientApp() {
        // Default constructor
    }

    /**
     * Returns the shared {@link ClientService} instance created during startup, or {@code null} if
     * none exists. The UI may call this to reuse the connection that already performed the initial
     * LOGIN.
     */
    public static ClientService getSharedClientService() {
        return sharedClientService;
    }

    private static void setSharedClientService(ClientService cs) {
        sharedClientService = cs;
    }

    /**
     * Starts the client application with the given address.
     *
     * @param arg Address in the format "ip:port".
     * @throws IllegalArgumentException if the address format is invalid.
     */
    public static void start(String arg, String username) {
        String[] parts = arg.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Address must be in format <ip>:<port>.");
        }
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        LOGGER.info("You've selected the client. It will connect port {} at host {}", port, host);
        System.setProperty("casono.server.host", host);
        System.setProperty("casono.server.port", String.valueOf(port));
        // If a username was provided, create a shared connection and perform
        // the initial LOGIN asynchronously on that connection. The connection
        // is intentionally NOT closed so the UI can reuse it.
        if (username != null && !username.isBlank()) {
            try {
                ClientService clientService = new ClientService(host, port);
                setSharedClientService(clientService);

                java.util.concurrent.ExecutorService bg =
                        java.util.concurrent.Executors.newSingleThreadExecutor(
                                r -> {
                                    Thread t = new Thread(r);
                                    t.setDaemon(true);
                                    t.setName("casono-startup-login");
                                    return t;
                                });
                bg.submit(
                        () -> {
                            try {
                                LobbyClient lobbyClient = new LobbyClient(clientService);
                                LoginResult res = lobbyClient.login(username);
                                LOGGER.info(
                                        "Assigned username='{}' id={}",
                                        res.getUsername(),
                                        res.getId());
                            } catch (RuntimeException e) {
                                LOGGER.warn("Startup login failed: {}", e.getMessage());
                            } catch (Exception e) {
                                LOGGER.warn("Unexpected error during startup login", e);
                            } finally {
                                bg.shutdown();
                            }
                        });
            } catch (RuntimeException e) {
                LOGGER.warn(
                        "Could not establish initial connection for startup login: {}",
                        e.getMessage());
                // UI will create its own connection when it initializes
            }
        }

        if (username != null && !username.isBlank()) {
            Launcher.main(new String[] {arg, username});
        } else {
            Launcher.main(new String[] {arg});
        }
    }

    /**
     * Main entry point. Usage: <host:port> [username]
     *
     * <p>If a username is provided the application attempts a startup LOGIN on the shared
     * connection; the UI will still start and will reuse the connection when possible. The username
     * is not propagated via a system property for UI display.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Address argument required: <host:port>");
        }
        // Optional username argument
        String username = args.length > 1 && args[1] != null && !args[1].isBlank() ? args[1] : null;
        start(args[0], username);
    }
}
