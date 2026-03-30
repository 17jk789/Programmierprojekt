package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.List;

public record RawRequest(String command, List<RequestParameter> parameters) {}
