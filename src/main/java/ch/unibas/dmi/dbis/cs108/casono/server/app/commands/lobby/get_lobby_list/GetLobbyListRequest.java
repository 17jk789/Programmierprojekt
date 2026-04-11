package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request object for the `GET_LOBBY_LIST` command.
 *
 * <p>Contains only the request context; this command does not require parameters.
 */
public class GetLobbyListRequest extends Request {
    public GetLobbyListRequest(RequestContext context) {
        super(context);
    }
}
