package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/** Parses CHANGE_USERNAME requests. */
public class ChangeUsernameParser implements CommandParser<ChangeUsernameRequest> {
    @Override
    public ChangeUsernameRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        return new ChangeUsernameRequest(primitiveRequest.context(), accessor.require("USERNAME"));
    }
}
