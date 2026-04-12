package ch.unibas.dmi.dbis.cs108.casono;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.server.ServerApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Main entry point for Casono application. Handles client and server startup. */
public final class Main {

    private static final int MIN_ARGS_FOR_USERNAME = 3;
    private static final int ARGS_COUNT_SERVER = 2;
    private static final int ARGS_COUNT_CLIENT_MIN = 2;
    private static final int ARGS_COUNT_CLIENT_WITH_USER = 3;

    /**
     * Main entry point for Casono.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (!isValid(args)) {
            printUsage();
            System.exit(1);
        }
        switch (args[0]) {
            case "server" -> ServerApp.start(args[1]);
            case "client" -> {
                String address = args[1];
                String username = args.length >= MIN_ARGS_FOR_USERNAME ? args[2] : null;
                ClientApp.start(address, username);
            }
            default -> {
                printUsage();
                System.exit(1);
            }
        }
    }

    private static boolean isValid(String[] args) {
        if (args.length < ARGS_COUNT_SERVER) {
            return false;
        }

        return switch (args[0]) {
            case "server" -> args.length == ARGS_COUNT_SERVER;
            case "client" ->
                    args.length == ARGS_COUNT_CLIENT_MIN
                            || args.length == ARGS_COUNT_CLIENT_WITH_USER;
            default -> false;
        };
    }

    private static void printUsage() {
        Logger logger = LogManager.getLogger(Main.class);
        logger.fatal(
                """
                Usage:
                  java -jar xyz.jar server <listenPort>
                  java -jar xyz.jar client <serverIp>:<serverPort> [<username>]
                """);
    }
}
