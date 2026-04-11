package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

/** Sends ID of newly created lobby back to client. */
public class CreateLobbyResponse extends SuccessResponse {
    public CreateLobbyResponse(RequestContext context, int lobbyId) {
        super(
                context,
                new ResponseBodyBuilder().param("LOBBY_ID", String.valueOf(lobbyId)).build());
    }
}
