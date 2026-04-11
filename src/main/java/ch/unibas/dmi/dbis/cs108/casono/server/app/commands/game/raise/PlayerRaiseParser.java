package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `RAISE` command.
 *
 * <p>
 * Parses the request parameters and builds a {@link PlayerRaiseRequest}.
 * Expected parameters:
 *
 * <ul>
 * <li>`GAME_ID` (optional) - numeric id of the lobby/game to target
 * <li>`AMOUNT` (required) - amount to raise
 * </ul>
 */
public class PlayerRaiseParser implements CommandParser<PlayerRaiseRequest> {
    @Override
    public PlayerRaiseRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());

        Integer gameId = null;
        try {
            gameId = accessor.optional("GAME_ID", null, Integer::parseInt);
        } catch (Exception e) {
            // parse handled by framework
        }

        int amount = accessor.require("AMOUNT", Integer::parseInt);

        return new PlayerRaiseRequest(primitiveRequest.context(), gameId, amount);
    }
}
