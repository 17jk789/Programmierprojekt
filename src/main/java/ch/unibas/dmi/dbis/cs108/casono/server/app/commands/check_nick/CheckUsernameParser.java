package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class CheckUsernameParser implements CommandParser<CheckUsernameRequest> {
    @Override
    public CheckUsernameRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());
        return new CheckUsernameRequest(primitiveRequest.context(), accessor.require("USERNAME"));
    }
}
