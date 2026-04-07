package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetNextMessageParser implements CommandParser<GetNextMessageRequest> {
    @Override
    public GetNextMessageRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());
        return new GetNextMessageRequest(primitiveRequest.context());
    }
}
