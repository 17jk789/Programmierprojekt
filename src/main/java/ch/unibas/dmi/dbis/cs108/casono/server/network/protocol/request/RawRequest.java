package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request;

import java.util.List;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;

public record RawRequest(String command, List<RequestParameter> parameters) {}
