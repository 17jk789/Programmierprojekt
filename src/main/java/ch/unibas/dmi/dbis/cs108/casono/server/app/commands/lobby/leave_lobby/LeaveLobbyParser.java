package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.leave_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `LEAVE_LOBBY` command.
 *
 * <p>Expected request parameters:
 *
 * <ul>
 *   <li>`ID` (int) — numeric lobby identifier (required)
 * </ul>
 *
 * <p>The parser builds a {@link LeaveLobbyRequest} containing the parsed lobby id and the original
 * request context.
 */
public class LeaveLobbyParser implements CommandParser<LeaveLobbyRequest> {
    /**
     * Parse the incoming primitive request into a {@link LeaveLobbyRequest}.
     *
     * @param primitiveRequest the raw primitive request
     * @return a {@link LeaveLobbyRequest} with the parsed lobby id and context
     */
    @Override
    public LeaveLobbyRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        int id = accessor.require("ID", Integer::parseInt);
        return new LeaveLobbyRequest(primitiveRequest.context(), id);
    }
}
