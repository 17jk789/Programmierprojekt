package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request for the `CALL` command.
 *
 * <p>Contains an optional `gameId` when the client targets a specific lobby, otherwise the server
 * will resolve the lobby from the requesting session's user.
 */
public class PlayerCallRequest extends Request {
    private final Integer gameId;

    /**
     * Creates a new {@code PlayerCallRequest}.
     *
     * @param context the request context
     * @param gameId optional game id of the targeted lobby (may be {@code null})
     */
    public PlayerCallRequest(RequestContext context, Integer gameId) {
        super(context);
        this.gameId = gameId;
    }

    /**
     * Returns the optional target game id.
     *
     * @return the game id or {@code null} if not provided
     */
    public Integer getGameId() {
        return gameId;
    }
}
