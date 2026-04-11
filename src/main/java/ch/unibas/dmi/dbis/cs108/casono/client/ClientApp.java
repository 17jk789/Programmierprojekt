package ch.unibas.dmi.dbis.cs108.casono.client;

/**
 * Entry point for the Casono client application. Handles client startup and connection parameters.
 */
import ch.unibas.dmi.dbis.cs108.casono.client.ui.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the Casono client application. Handles client startup and connection parameters.
 *
 * <p>Default constructor for the application.
 */
public class ClientApp {

    private static final Logger LOGGER = LogManager.getLogger(ClientApp.class);

    /** Default constructor. */
    public ClientApp() {
        // Default constructor
    }

    /**
     * Starts the client application with the given address.
     *
     * @param arg Address in the format "ip:port".
     * @throws IllegalArgumentException if the address format is invalid.
     */
    public static void start(String arg) {
        String[] parts = arg.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Address must be in format <ip>:<port>.");
        }
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        LOGGER.info("You've selected the client. It will connect port {} at host {}", port, host);
        // Expose the chosen host/port to the UI via system properties so controllers
        // (which read System.getProperty("casono.server.host"/"casono.server.port"))
        // can obtain the correct connection information.
        System.setProperty("casono.server.host", host);
        System.setProperty("casono.server.port", Integer.toString(port));
        // Forward the original address argument to the launcher as well.
        Launcher.main(new String[] { arg });
    }

    public static void main(String[] args) {
        start(args[0]);
    }
}
