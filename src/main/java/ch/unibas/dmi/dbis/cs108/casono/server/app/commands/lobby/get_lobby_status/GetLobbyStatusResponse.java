package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;

/** Response with lobby player listing and simple READY placeholders. */
public class GetLobbyStatusResponse extends SuccessResponse {
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
