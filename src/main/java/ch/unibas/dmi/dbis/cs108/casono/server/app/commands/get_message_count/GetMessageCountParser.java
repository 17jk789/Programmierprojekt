package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetMessageCountParser implements CommandParser<GetMessageCountRequest> {
    @Override
    public GetMessageCountRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());
        return new GetMessageCountRequest(primitiveRequest.context());
    }
}
