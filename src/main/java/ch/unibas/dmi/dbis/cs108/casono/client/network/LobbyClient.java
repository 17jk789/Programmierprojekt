package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.ArrayList;
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

        // Prefer explicit STATUS parameter when available
        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);
        for (RequestParameter p : params) {
            if ("STATUS".equalsIgnoreCase(p.key())) {
                return p.value();
            }
        }

        // Fallback: some servers may return a plain token as the first line
        if (!lines.isEmpty()) {
            String first = lines.get(0).trim();
            if ("CREATED".equalsIgnoreCase(first) || "RUNNING".equalsIgnoreCase(first)) {
                return first.toUpperCase();
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

    /** Fetches the global highscores from the server. */
    public List<String> getHighscores() {
        List<String> lines = client.processCommand("GET_HIGHSCORES");
        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);

        List<String> entries = new ArrayList<>();
        for (RequestParameter p : params) {
            if ("HIGHSCORE".equalsIgnoreCase(p.key())) {
                entries.add(p.value());
            }
        }

        return entries;
    }

    /** Clears all global highscores on the server. */
    public void clearHighscores() {
        client.processCommand("CLEAR_HIGHSCORES");
    }

    /**
     * Request the server to return the id of the lobby that the client is currently in.
     *
     * @return The id of the lobby that the client is currently in, as returned by the server.
     */
    public int getLobbyId() {
        List<String> lines = client.processCommand("GET_LOBBY_ID");
        if (lines.isEmpty()) {
            throw new RuntimeException("GET_LOBBY_ID returned empty response");
        }
        try {
            return Integer.parseInt(lines.get(0).trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid GET_LOBBY_ID response: " + lines, e);
        }
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
        String command = buildLoginCommand(user);
        List<String> lines = client.processCommand(command);

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

    private String buildLoginCommand(String username) {
        if (username == null || username.isBlank()) {
            return "LOGIN";
        }
        return "LOGIN USERNAME=" + username.trim();
    }

    /**
     * Changes the username for the currently logged-in session.
     *
     * @param newUsername desired new username
     * @return a {@link LoginResult} containing assigned username and id as returned by the server
     */
    public LoginResult changeUsername(String newUsername) {
        List<String> lines = client.processCommand("CHANGE_USERNAME USERNAME=" + newUsername);

        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);

        String assigned = newUsername;
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

    /**
     * Request the server for the list of available lobbies.
     *
     * @return list of LobbyInfo objects representing current lobbies
     */
    public List<LobbyInfo> getLobbyList() {
        List<String> lines = client.processCommand("GET_LOBBY_LIST");

        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);

        List<LobbyInfo> result = new ArrayList<>();

        Integer currentId = null;
        String currentName = null;
        Integer currentPlayerCount = null;

        for (RequestParameter p : params) {
            String key = p.key().toUpperCase();
            String val = p.value();
            switch (key) {
                case "ID":
                    if (currentId != null) {
                        result.add(
                                new LobbyInfo(
                                        currentId,
                                        currentName,
                                        currentPlayerCount == null ? 0 : currentPlayerCount));
                        currentName = null;
                        currentPlayerCount = null;
                    }
                    try {
                        currentId = Integer.parseInt(val);
                    } catch (NumberFormatException e) {
                        currentId = null;
                    }
                    break;
                case "NAME":
                    currentName = val;
                    break;
                case "PLAYER_COUNT":
                    try {
                        currentPlayerCount = Integer.parseInt(val);
                    } catch (NumberFormatException e) {
                        currentPlayerCount = 0;
                    }
                    break;
                default:
                    break;
            }
        }

        if (currentId != null) {
            result.add(
                    new LobbyInfo(
                            currentId,
                            currentName,
                            currentPlayerCount == null ? 0 : currentPlayerCount));
        }

        return result;
    }

    /**
     * Fetches the current lobby members from GET_LOBBY_STATUS and returns their usernames in order.
     *
     * @param lobbyId id of the lobby to inspect
     * @return list of usernames currently known in the lobby
     */
    public List<String> fetchLobbyPlayerNames(int lobbyId) {
        List<String> lines = client.processCommand("GET_LOBBY_STATUS ID=" + lobbyId);
        List<RequestParameter> params = ClientService.convertToRequestParameters(lines);

        List<String> names = new ArrayList<>();
        for (RequestParameter parameter : params) {
            if (!"USERNAME".equalsIgnoreCase(parameter.key())) {
                continue;
            }
            String name = parameter.value();
            if (name != null) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty() && !names.contains(trimmed)) {
                    names.add(trimmed);
                }
            }
        }

        return names;
    }

    /** Simple data holder for lobby metadata returned by the server. */
    public static final class LobbyInfo {
        public final int id;
        public final String name;
        public final int playerCount;

        public LobbyInfo(int id, String name, int playerCount) {
            this.id = id;
            this.name = name;
            this.playerCount = playerCount;
        }
    }
}
