package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder;

import java.util.List;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;

/**
 * Immutable container for the structured content of a {@link Response}.
 *
 * <p>A {@code ResponseBody} holds an ordered list of {@link ResponseNode} items (parameters and
 * blocks). Use {@link #builder()} to construct instances.
 */
public class ResponseBody {
    private final List<ResponseNode> nodes;

    /**
     * Package-private constructor used by {@link ResponseBodyBuilder}.
     *
     * @param nodes the list of response nodes to include in this body
     */
    ResponseBody(List<ResponseNode> nodes) {
        this.nodes = List.copyOf(nodes);
    }

    /**
     * Create a new {@link ResponseBodyBuilder} for assembling a response body.
     *
     * @return a fresh builder instance
     */
    public static ResponseBodyBuilder builder() {
        return new ResponseBodyBuilder();
    }

    /**
     * Returns the ordered list of {@link ResponseNode} elements contained in this body.
     *
     * @return an immutable list of nodes
     */
    public List<ResponseNode> nodes() {
        return nodes;
    }
}
