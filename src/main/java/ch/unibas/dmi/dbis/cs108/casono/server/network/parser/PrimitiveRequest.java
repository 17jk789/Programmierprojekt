package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import java.util.List;

/** Created by the ProtocolParser to allow easy access to the request contents */
public record PrimitiveRequest(
        RequestContext context, String command, List<Parameter> parameters) {}
