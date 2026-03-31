package ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;

/**
 * Parser to convert the PrimitiveRequest to a Request and performing checks for required fields and
 * data types
 */
public interface CommandParser<T extends Request> {
    /**
     * Parses the provided PrimitiveRequest into a command-specific request
     *
     * @param primitiveRequest
     * @return
     */
    T parse(PrimitiveRequest primitiveRequest);
}
