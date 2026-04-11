package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class JoinLobbyParser implements CommandParser<JoinLobbyRequest> {
    @Override
    public JoinLobbyRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        int id = accessor.require("ID", Integer::parseInt);
        return new JoinLobbyRequest(primitiveRequest.context(), id);
    }
}
