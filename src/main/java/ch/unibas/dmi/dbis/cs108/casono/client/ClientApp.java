package ch.unibas.dmi.dbis.cs108.casono.client;

/**
 * Entry point for the Casono client application.
 * Handles client startup and connection parameters.
 */
import ch.unibas.dmi.dbis.cs108.casono.client.ui.Launcher;
/**
 * Entry point for the Casono client application.
 * Handles client startup and connection parameters.
 * <p>
 * Standardkonstruktor für die Anwendung.
 */
public class ClientApp {

    /**
     * Standardkonstruktor.
     */
    public ClientApp() {
        // Standardkonstruktor
    }
    /**
     * Starts the client application with the given address.
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

        System.out.println("You've selected the client. It will connect port " + port + " at host " + host);
        Launcher.main(new String[]{});
    }
}
