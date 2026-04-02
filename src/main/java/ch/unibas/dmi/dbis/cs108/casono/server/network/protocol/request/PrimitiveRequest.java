package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.List;

/** Created by the ProtocolParser to allow easy access to the request contents */
public record PrimitiveRequest(
        RequestContext context, String command, List<RequestParameter> parameters) {}
