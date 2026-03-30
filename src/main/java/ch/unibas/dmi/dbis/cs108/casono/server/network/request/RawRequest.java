package ch.unibas.dmi.dbis.cs108.casono.server.network.request;

import java.util.List;

import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.RequestParameter;

public record RawRequest(String command, List<RequestParameter> parameters) {}
