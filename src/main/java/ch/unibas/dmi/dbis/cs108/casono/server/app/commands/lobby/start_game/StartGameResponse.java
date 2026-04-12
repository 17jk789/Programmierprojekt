package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

/**
 * Success response for {@code START_GAME} containing the started game's identifier and an
 * informational message.
 */
public class StartGameResponse extends SuccessResponse {
    /**
     * Create a standard start-game response with a default success message.
     *
     * @param context the request context
     * @param gameId numeric id of the started game (uses lobby id)
     */
    public StartGameResponse(RequestContext context, int gameId) {
        this(context, gameId, "Game started successfully");
    }

    /**
     * Create a start-game response with a custom message.
     *
     * @param context the request context
     * @param gameId numeric id of the started game (uses lobby id)
     * @param message informational message for the client
     */
    public StartGameResponse(RequestContext context, int gameId, String message) {
        super(
                context,
                ResponseBody.builder().param("GAME", gameId).param("MESSAGE", message).build());
    }
}
