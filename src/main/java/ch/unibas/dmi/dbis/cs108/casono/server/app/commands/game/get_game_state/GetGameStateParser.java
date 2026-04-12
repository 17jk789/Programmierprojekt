package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetGameStateParser implements CommandParser<GetGameStateRequest> {
    @Override
    public GetGameStateRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        Integer gameId = accessor.optional("GAME_ID", null, Integer::parseInt);
        String username = accessor.optional("USERNAME", null);
        return new GetGameStateRequest(primitiveRequest.context(), username, gameId);
    }
}
