package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import java.util.List;

public record ResponseBlock(String tag, List<ResponseNode> children) implements ResponseNode {}
