package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/**
 * Parser for the Ping command.
 *
 * <p>Converts a low-level {@link PrimitiveRequest} into a {@link PingRequest}.
 */
public class PingParser implements CommandParser<PingRequest> {
    /**
     * Parse the given primitive request into a {@link PingRequest}.
     *
     * @param primitiveRequest the raw request to parse
     * @return {@link PingRequest}
     */
    @Override
    public PingRequest parse(PrimitiveRequest primitiveRequest) {
        return new PingRequest(primitiveRequest.context());
    }
}
