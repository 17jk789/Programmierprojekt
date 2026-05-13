package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.leave_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request data for the `LEAVE_LOBBY` command.
 *
 * <p>Contains the lobby id the client wants to leave and inherits the {@link Request} contextual
 * information.
 */
public class LeaveLobbyRequest extends Request {
    private final int id;

    /**
     * Create a new {@link LeaveLobbyRequest}.
     *
     * @param context the request context
     * @param id numeric lobby id to leave
     */
    public LeaveLobbyRequest(RequestContext context, int id) {
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
