package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import java.util.List;

public record RawRequest(String command, List<Parameter> parameters) {}
