package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

/**
 * Success response for `GET_LOBBY_STATUS`.
 *
 * <p>Builds a response with a single `LOBBY` block containing `ID`, `NAME` and a nested `PLAYERS`
 * collection with repeated `PLAYER` blocks (`USERNAME`, `READY`).
 */
public class GetLobbyStatusResponse extends SuccessResponse {
    /**
     * Create a new response for the provided {@link Lobby}.
     *
     * @param context request context to use for the response
     * @param lobby lobby domain object containing id, name and players
     */
    public GetLobbyStatusResponse(RequestContext context, Lobby lobby) {
        super(
                context,
                ResponseBody.builder()
                        .block(
                                "LOBBY",
                                lb -> {
                                    lb.param("ID", lobby.getId().value());
                                    lb.param("NAME", lobby.getName());
                                    lb.block(
                                            "PLAYERS",
                                            players -> {
                                                for (String p : lobby.getPlayerNames()) {
                                                    players.block(
                                                            "PLAYER",
                                                            pb -> {
                                                                pb.param("USERNAME", p);
                                                                pb.param("READY", "false");
                                                            });
                                                }
                                            });
                                })
                        .build());
    }
}
