package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.List;

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

    public ClientService getClientService() {
        return client;
    }

    /**
     * Fetch the current status of the lobby with the given id from the server.
     *
     * @param lobbyId The id of the lobby to fetch the status for.
     * @return A string representing the current status of the lobby, as returned by the server.
     */
    public String fetchLobbyStatusString(int lobbyId) {
        List<String> lines = client.processCommand("GET_LOBBY_STATUS ID=" + lobbyId);

        // Prefer an explicit STATUS parameter if provided by the server
        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);
        for (RequestParameter p : params) {
            if ("STATUS".equalsIgnoreCase(p.key())) {
                return p.value();
            }
        }

        // Fallback: look for plain status tokens in the body
        for (String l : lines) {
            String t = l.trim();
            if ("CREATED".equalsIgnoreCase(t) || "RUNNING".equalsIgnoreCase(t)) {
                return t;
            }
        }

        return null;
    }

    /**
     * Request the server to create a new lobby and return the id of the newly created lobby.
     *
     * @return The id of the newly created lobby, as returned by the server.
     */
    public int createLobby() {
        List<String> lines = client.processCommand("CREATE_LOBBY");

        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);
        for (RequestParameter p : params) {
            if ("LOBBY_ID".equalsIgnoreCase(p.key())) {
                return Integer.parseInt(p.value());
            }
        }

        // Fallback for simple legacy/test servers that return the id as a single plain
        // line
        if (!lines.isEmpty()) {
            try {
                return Integer.parseInt(lines.get(0));
            } catch (NumberFormatException ignored) {
            }
        }

        throw new RuntimeException("No LOBBY_ID in response: " + lines);
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

    /**
     * Logs in to the server with the given username by sending a "LOGIN" command.
     *
     * @param user The username to log in with.
     * @return a {@link LoginResult} containing the assigned username and id as returned by the
     *     server
     */
    public LoginResult login(String user) {
        List<String> lines = client.processCommand("LOGIN USERNAME=" + user);

        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);

        String assigned = user;
        String id = null;
        for (RequestParameter p : params) {
            if ("USERNAME".equalsIgnoreCase(p.key())) {
                assigned = p.value();
            } else if ("ID".equalsIgnoreCase(p.key())) {
                id = p.value();
            }
        }
        return new LoginResult(assigned, id);
    }
}
