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
 * <p>Responsibilities: - Parse CLI args (host:port, optional username) - Store username centrally
 * so UI can reuse it - Create a shared ClientService and do startup LOGIN
 */
public class ClientApp {

    private static final Logger LOGGER = LogManager.getLogger(ClientApp.class.getSimpleName());

    /** Shared client connection used when a username is provided at startup. */
    private static volatile ClientService sharedClientService;

    private static volatile String sharedUsername;

    /** Default constructor. */
    public ClientApp() {
        // Default constructor
    }

    public static ClientService getSharedClientService() {
        return sharedClientService;
    }

    private static void setSharedClientService(ClientService cs) {
        sharedClientService = cs;
    }

    public static String getSharedUsername() {
        return sharedUsername;
    }

    public static void updateSharedUsername(String username) {
        setSharedUsername(normalizeUsername(username));
    }

    private static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return username.trim();
    }

    private static void setSharedUsername(String username) {
        sharedUsername = username;
        LOGGER.info("sharedUsername set to '{}'", getSharedUsername());
    }

    public static void start(String arg, String username) {
        String[] hostPort = parseHostAndPort(arg);
        String host = hostPort[0];
        int port = Integer.parseInt(hostPort[1]);
        String normalizedUsername = normalizeUsername(username);

        configureServerProperties(host, port);
        setSharedUsername(normalizedUsername);

        performStartupLogin(host, port, normalizedUsername);

        launchUI(arg);
    }

    private static String[] parseHostAndPort(String arg) {
        String[] parts = arg.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("The address must be in the format <ip>:<port>.");
        }
        return parts;
    }

    private static void configureServerProperties(String host, int port) {
        LOGGER.info("You've selected the client. It will connect port {} at host {}", port, host);
        System.setProperty("casono.server.host", host);
        System.setProperty("casono.server.port", String.valueOf(port));
    }

    private static void performStartupLogin(String host, int port, String username) {
        try {
            ClientService clientService = new ClientService(host, port);
            setSharedClientService(clientService);
            LobbyClient lobbyClient = new LobbyClient(clientService);
            LoginResult res = lobbyClient.login(username);

            if (res != null && res.getUsername() != null && !res.getUsername().isBlank()) {
                setSharedUsername(res.getUsername().trim());
            }

            String assignedUsername = null;
            String assignedId = null;
            if (res != null) {
                assignedUsername = res.getUsername();
                assignedId = res.getId();
            }
            LOGGER.info("Assigned username='{}' id={}", assignedUsername, assignedId);
        } catch (RuntimeException e) {
            LOGGER.warn(
                    "Could not establish initial connection for startup login: {}", e.getMessage());
        }
    }

    private static void launchUI(String arg) {
        String username = getSharedUsername();
        if (username == null || username.isBlank()) {
            Launcher.main(new String[] {arg});
            return;
        }
        Launcher.main(new String[] {arg, username});
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Address argument required: <host:port>");
        }

        String username = null;
        if (args.length > 1) {
            username = normalizeUsername(args[1]);
        }

        start(args[0], username);
    }
}
