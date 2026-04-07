package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class LogoutParser implements CommandParser<LogoutRequest> {
    @Override
    public LogoutRequest parse(PrimitiveRequest primitiveRequest) {
        return new LogoutRequest(primitiveRequest.context());
    }
}
