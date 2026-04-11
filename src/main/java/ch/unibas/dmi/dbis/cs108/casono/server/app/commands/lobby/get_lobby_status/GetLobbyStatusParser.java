package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetLobbyStatusParser implements CommandParser<GetLobbyStatusRequest> {
    /**
     * Parse the incoming primitive request into a {@link GetLobbyStatusRequest}.
     *
     * <p>Supported optional parameters:
     *
     * <ul>
     *   <li>`ID` (int) — numeric lobby id to query
     *   <li>`USERNAME` (String) — username to lookup the lobby for
     * </ul>
     *
     * If both are omitted the handler may require a logged-in session.
     *
     * @param primitiveRequest raw request containing parameters and context
     * @return parsed {@link GetLobbyStatusRequest}
     */
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
