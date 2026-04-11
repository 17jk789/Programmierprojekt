package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request data for the `JOIN_LOBBY` command.
 *
 * <p>
 * Contains the lobby id the client wants to join and inherits the
 * {@link Request} contextual information.
 */
public class JoinLobbyRequest extends Request {
    private final int id;

    /**
     * Create a new {@link JoinLobbyRequest}.
     *
     * @param context the request context
     * @param id      numeric lobby id to join
     */
    public JoinLobbyRequest(RequestContext context, int id) {
        super(context);
        this.id = id;
    }

    /**
     * Returns the lobby id requested by the client.
     *
     * @return numeric lobby id
     */
    public int getId() {
        return id;
    }
}
