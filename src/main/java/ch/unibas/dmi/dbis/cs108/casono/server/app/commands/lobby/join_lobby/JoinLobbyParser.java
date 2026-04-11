package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `JOIN_LOBBY` command.
 *
 * <p>
 * Expected request parameters:
 * <ul>
 * <li>`ID` (int) — numeric lobby identifier (required)
 * </ul>
 *
 * <p>
 * The parser builds a {@link JoinLobbyRequest} containing the parsed lobby id
 * and the original request context.
 */
public class JoinLobbyParser implements CommandParser<JoinLobbyRequest> {
    /**
     * Parse the incoming primitive request into a {@link JoinLobbyRequest}.
     *
     * @param primitiveRequest the raw primitive request
     * @return a {@link JoinLobbyRequest} with the parsed lobby id and context
     */
    @Override
    public JoinLobbyRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());
        int id = accessor.require("ID", Integer::parseInt);
        return new JoinLobbyRequest(primitiveRequest.context(), id);
    }
}
