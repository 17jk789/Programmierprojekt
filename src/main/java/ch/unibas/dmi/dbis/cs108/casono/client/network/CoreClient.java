package ch.unibas.dmi.dbis.cs108.casono.client.network;

/**
 * The CoreClient class provides basic functionalities for communicating with
 * the
 * server, such as sending a ping command to check connectivity and logging in
 * with a username. It uses the ClientService to send commands and receive
 * responses from the server.
 */
public class CoreClient {
    private final ClientService clientService;
    /**
     * Constructs a CoreClient with the given ClientService for communication.
     *
     * @param clientservice The ClientService instance used to send commands and
     *                      receive responses from the server.
     */
    public CoreClient(ClientService clientservice) {
        this.clientService = clientservice;
    }

    /**
     * Sends a "PING" command to the server to check connectivity. The server
     * should respond with a "PONG" message if the connection is successful.
     */
    public void ping() {
        clientService.processCommand("PING");
    }

    /**
     * Logs in to the server with the given username by sending a "LOGIN" command.
     *
     * @param user The username to log in with.
     */
    public void login(String user) {
        clientService.processCommand("LOGIN USERNAME=" + user);
    }
}
