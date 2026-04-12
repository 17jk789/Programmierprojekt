package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class GetLobbyStatusRequest extends Request {
    private final Integer id;
    private final String username;

    /**
     * Create a new GetLobbyStatusRequest.
     *
     * @param context request context (session id, source, etc.)
     * @param id optional numeric lobby id to query, or null
     * @param username optional username to query the lobby for, or null
     */
    public GetLobbyStatusRequest(RequestContext context, Integer id, String username) {
        super(context);
        this.id = id;
        this.username = username;
    }

    /** Returns the optional lobby id. */
    public Integer getId() {
        return id;
    }

    /** Returns the optional username. */
    public String getUsername() {
        return username;
    }
}
