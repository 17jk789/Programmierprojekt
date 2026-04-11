package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetLobbyStatusParser implements CommandParser<GetLobbyStatusRequest> {
    @Override
    public GetLobbyStatusRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        Integer id = null;
        try {
            id = accessor.optional("ID", null, Integer::parseInt);
        } catch (Exception e) {
            // parse error handled elsewhere
        }
        String username = accessor.optional("USERNAME", null);
        return new GetLobbyStatusRequest(primitiveRequest.context(), id, username);
    }
}
