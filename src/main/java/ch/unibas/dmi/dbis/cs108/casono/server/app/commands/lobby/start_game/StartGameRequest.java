package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request to start a game in a specific lobby.
 *
 * <p>The `START_GAME` request requires the numeric lobby id parameter `ID` identifying the target
 * lobby. The request carries the usual {@link RequestContext} (session id, request id) via the base
 * class.
 */
public class StartGameRequest extends Request {
    private final int id;

    /**
     * Create a new {@link StartGameRequest}.
     *
     * @param context the request context
     * @param id numeric lobby id to start the game in
     */
    public StartGameRequest(RequestContext context, int id) {
        super(context);
        this.id = id;
    }

    /**
     * Returns the numeric lobby id provided by the client.
     *
     * @return lobby id
     */
    public int getId() {
        return id;
    }
}
