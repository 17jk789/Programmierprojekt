package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetMessageCountParser implements CommandParser<GetMessageCountRequest> {
    /**
     * Parses a raw {@link PrimitiveRequest} into a {@link GetMessageCountRequest}.
     * This method wraps the request context from the network layer into a
     * structured message count request object.
     *
     * @param primitiveRequest The raw request containing parameters and context from the network.
     * @return A new {@link GetMessageCountRequest} instance.
     */
    @Override
    public GetMessageCountRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        return new GetMessageCountRequest(primitiveRequest.context());
    }
}
