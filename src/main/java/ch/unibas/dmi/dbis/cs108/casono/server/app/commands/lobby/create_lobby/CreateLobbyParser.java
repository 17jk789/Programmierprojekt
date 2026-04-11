package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class CreateLobbyParser implements CommandParser<CreateLobbyRequest> {
    @Override
    public CreateLobbyRequest parse(PrimitiveRequest primitiveRequest) {
        return new CreateLobbyRequest(primitiveRequest.context());
    }
}
