package ch.unibas.dmi.dbis.cs108.casono.client;

public class ClientApp {
    public static void start(String arg) {
        String[] parts = arg.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Address must be in format <ip>:<port>.");
        }
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        System.out.println("You've selected the client. It will connect port " + port + " at host " + host);
    }
}
