package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import java.util.List;

public class ResponseBody {
    private final List<ResponseNode> nodes;

    ResponseBody(List<ResponseNode> nodes) {
        this.nodes = List.copyOf(nodes);
    }

    public static ResponseBodyBuilder builder() {
        return new ResponseBodyBuilder();
    }

    public List<ResponseNode> nodes() {
        return nodes;
    }
}
