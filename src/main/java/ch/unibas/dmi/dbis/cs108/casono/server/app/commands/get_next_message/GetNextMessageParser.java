package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GetNextMessageParser implements CommandParser<GetNextMessageRequest> {
    /**
     * Parses a raw {@link PrimitiveRequest} into a {@link GetNextMessageRequest}. This method
     * initializes a parameter accessor (though not currently used for extraction) and returns a
     * structured request object containing the original request context.
     *
     * @param primitiveRequest The raw request containing parameters and context from the network.
     * @return A new {@link GetNextMessageRequest} instance.
     */
    @Override
    public GetNextMessageRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());
        return new GetNextMessageRequest(primitiveRequest.context());
    }
}
