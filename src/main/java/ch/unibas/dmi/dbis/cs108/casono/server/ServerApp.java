package ch.unibas.dmi.dbis.cs108.casono.server;

public class ServerApp {
    public static void start(String arg) {
        int port = Integer.parseInt(arg);
        System.out.println("You've selected the server. It will accept connections at port " + port);
    }
}
