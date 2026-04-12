package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class CreateLobbyRequest extends Request {
    public CreateLobbyRequest(RequestContext context) {
        super(context);
    }
}
