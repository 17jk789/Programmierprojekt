package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `START_GAME` command.
 *
 * <p>Expected request parameters:
 *
 * <ul>
 *   <li>`ID` (int) — numeric lobby identifier (required)
 * </ul>
 *
 * <p>The parser builds a {@link StartGameRequest} containing the parsed lobby id and the original
 * request context.
 */
public class StartGameParser implements CommandParser<StartGameRequest> {
    @Override
    public StartGameRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        int id = accessor.require("ID", Integer::parseInt);
        return new StartGameRequest(primitiveRequest.context(), id);
    }
}
