package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class PingParser implements CommandParser<PingRequest> {
    @Override
    public PingRequest parse(PrimitiveRequest primitiveRequest) {
        return new PingRequest(primitiveRequest.context());
    }
}
