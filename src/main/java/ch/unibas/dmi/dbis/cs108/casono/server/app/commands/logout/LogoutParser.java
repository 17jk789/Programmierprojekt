package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/** Parses a primitive request into a {@link LogoutRequest}. */
public class LogoutParser implements CommandParser<LogoutRequest> {

    /**
     * Parses a primitive request into a LogoutRequest.
     *
     * @param primitiveRequest the request to parse
     * @return the created {@link LogoutRequest}
     */
    @Override
    public LogoutRequest parse(PrimitiveRequest primitiveRequest) {
        return new LogoutRequest(primitiveRequest.context());
    }
}
