package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/** Parses a primitive request into a {@link LoginRequest}. */
public class LoginParser implements CommandParser<LoginRequest> {
    /**
     * Extracts the required {@code USERNAME} parameter from the incoming request.
     *
     * @param primitiveRequest the request to parse
     * @return {@link LoginRequest} containing the username
     */
    @Override
    public LoginRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        return new LoginRequest(primitiveRequest.context(), accessor.optional("USERNAME", null));
    }
}
