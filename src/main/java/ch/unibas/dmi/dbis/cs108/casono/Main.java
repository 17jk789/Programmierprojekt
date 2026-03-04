package ch.unibas.dmi.dbis.cs108.casono;

import ch.unibas.dmi.dbis.cs108.casono.client.ClientApp;
import ch.unibas.dmi.dbis.cs108.casono.server.ServerApp;

public final class Main {

    public static void main(String[] args) {
        if (!isValid(args)) {
            printUsage();
            System.exit(1);
        }

        switch (args[0]) {
            case "server" -> ServerApp.start(args[1]);
            case "client" -> ClientApp.start(args[1]);
            default -> {
                printUsage();
                System.exit(1);
            }
        }
    }

    private static boolean isValid(String[] args) {
        if (args.length != 2) {
            return false;
        }

        return switch (args[0]) {
            case "server", "client" -> true;
            default -> false;
        };
    }

    private static void printUsage() {
        System.err.println("""
            Usage:
              java -jar xyz.jar server <listenPort>
              java -jar xyz.jar client <serverIp>:<serverPort>
            """);
    }
}
