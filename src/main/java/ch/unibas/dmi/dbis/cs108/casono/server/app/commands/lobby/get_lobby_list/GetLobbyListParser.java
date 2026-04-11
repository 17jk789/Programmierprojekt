package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/**
 * Parser for the `GET_LOBBY_LIST` command.
 *
 * <p>Parses the incoming primitive request and produces a {@link GetLobbyListRequest}. This command
 * takes no parameters.
 */
public class GetLobbyListParser implements CommandParser<GetLobbyListRequest> {
    @Override
    public GetLobbyListRequest parse(PrimitiveRequest primitiveRequest) {
        return new GetLobbyListRequest(primitiveRequest.context());
    }
}
