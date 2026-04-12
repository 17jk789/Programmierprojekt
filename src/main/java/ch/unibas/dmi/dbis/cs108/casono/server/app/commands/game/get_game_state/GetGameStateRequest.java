package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request object for `GET_GAME_STATE`.
 *
 * <p>Either `USERNAME` or `GAME_ID` may be provided by the client. If both are omitted the handler
 * will resolve the username from the session.
 */
public class GetGameStateRequest extends Request {
    private final String username;
    private final Integer gameId;

    public GetGameStateRequest(RequestContext context, String username, Integer gameId) {
        super(context);
        this.username = username;
        this.gameId = gameId;
    }

    /** Returns the optional username provided in the request. */
    public String getUsername() {
        return username;
    }

    /** Returns the optional game id provided in the request. */
    public Integer getGameId() {
        return gameId;
    }
}
