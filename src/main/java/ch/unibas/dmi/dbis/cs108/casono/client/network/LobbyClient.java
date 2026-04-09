package ch.unibas.dmi.dbis.cs108.casono.client.network;

/**
 * The LobbyClient class is responsible for communicating with the server to manage game lobbies. It
 * provides methods to create a lobby, join a lobby, and fetch the current status of a lobby by
 * sending appropriate commands to the server and processing the responses.
 */
public class LobbyClient {
    private final ClientService client;

    /**
     * Constructs a LobbyClient with the given ClientService for communication.
     *
     * @param client The ClientService instance used to send commands and receive responses from the
     *     server.
     */
    public LobbyClient(ClientService client) {
        this.client = client;
    }

    /**
     * Fetch the current status of the lobby with the given id from the server.
     *
     * @param lobbyId The id of the lobby to fetch the status for.
     * @return A string representing the current status of the lobby, as returned by the server.
     */
    public String fetchLobbyStatusString(int lobbyId) {
        return client.processCommand("GET_LOBBY_STATUS ID=" + lobbyId).getFirst();
    }

    /**
     * Request the server to create a new lobby and return the id of the newly created lobby.
     *
     * @return The id of the newly created lobby, as returned by the server.
     */
    public int createLobby() {
        String response = client.processCommand("CREATE_LOBBY").getFirst();
        return Integer.parseInt(response);
    }

    /**
     * Request the server to return the id of the lobby that the client is currently in.
     *
     * @return The id of the lobby that the client is currently in, as returned by the server.
     */
    public int getLobbyId() {
        String response = client.processCommand("GET_LOBBY_ID").getFirst();
        return Integer.parseInt(response);
    }

    /**
     * Request the server to join the lobby with the given id.
     *
     * @param lobbyId The id of the lobby to join.
     */
    public void joinLobby(int lobbyId) {
        client.processCommand("JOIN_LOBBY ID=" + lobbyId);
    }
}
