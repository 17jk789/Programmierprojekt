package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class JoinLobbyRequest extends Request {
    private final int id;

    public JoinLobbyRequest(RequestContext context, int id) {
        super(context);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
