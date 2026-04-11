package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import java.util.Collection;

/**
 * Success response for `GET_LOBBY_LIST`.
 *
 * <p>Builds a `LOBBIES` collection with repeated `LOBBY` blocks containing `ID`, `NAME` and
 * `PLAYER_COUNT`.
 */
public class GetLobbyListResponse extends SuccessResponse {
    public GetLobbyListResponse(RequestContext context, Collection<Lobby> lobbies) {
        super(
                context,
                ResponseBody.builder()
                        .block(
                                "LOBBIES",
                                lobbies_block -> {
                                    for (Lobby lobby : lobbies) {
                                        lobbies_block.block(
                                                "LOBBY",
                                                lb -> {
                                                    lb.param("ID", lobby.getId().value());
                                                    lb.param("NAME", lobby.getName());
                                                    lb.param(
                                                            "PLAYER_COUNT",
                                                            lobby.getPlayerNames().size());
                                                });
                                    }
                                })
                        .build());
    }
}
