package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `FOLD` command.
 *
 * <p>Accepts an optional `GAME_ID` parameter. If provided, the handler will target the specified
 * lobby; otherwise the server resolves the lobby by the requesting session's user.
 */
public class PlayerFoldParser implements CommandParser<PlayerFoldRequest> {
    @Override
    public PlayerFoldRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());

        Integer gameId = null;
        try {
            gameId = accessor.optional("GAME_ID", null, Integer::parseInt);
        } catch (Exception e) {
            // parse handled by framework
        }

        return new PlayerFoldRequest(primitiveRequest.context(), gameId);
    }
}
