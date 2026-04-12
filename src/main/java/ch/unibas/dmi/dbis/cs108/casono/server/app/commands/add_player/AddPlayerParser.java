package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.add_player;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/** Parser for the ADD_PLAYER command. */
public class AddPlayerParser implements CommandParser<AddPlayerRequest> {
    @Override
    public AddPlayerRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());

        String name = accessor.require("NAME");

        int chips = accessor.require("CHIPS", Integer::parseInt);

        return new AddPlayerRequest(primitiveRequest.context(), name, chips);
    }
}
