package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `CALL` command.
 *
 * <p>Accepts an optional `GAME_ID` parameter. If provided, the handler will target the specified
 * lobby; otherwise the server resolves the lobby by the requesting session's user.
 */
public class PlayerCallParser implements CommandParser<PlayerCallRequest> {
    @Override
    public PlayerCallRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());

        Integer gameId = null;
        try {
            gameId = accessor.optional("GAME_ID", null, Integer::parseInt);
        } catch (Exception e) {
            // parse errors handled by framework
        }

        return new PlayerCallRequest(primitiveRequest.context(), gameId);
    }
}
