package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for {@link ResponseBody} instances.
 *
 * <p>Provides methods to append parameter nodes and nested blocks and to produce an immutable
 * {@link ResponseBody} via {@link #build()}.
 */
public class ResponseBodyBuilder {
    private final List<ResponseNode> nodes = new ArrayList<>();

    /**
     * Add a key/value parameter to the response body under construction.
     *
     * @param key the parameter name
     * @param value the parameter value (will be converted to string when encoded)
     * @return this builder for fluent chaining
     */
    public ResponseBodyBuilder param(String key, Object value) {
        nodes.add(new ResponseParameter(key, value));
        return this;
    }

    /**
     * Add a nested block with the given tag. The provided consumer receives a child builder to
     * populate the block content.
     *
     * @param tag the block tag
     * @param content consumer that appends child nodes to the block
     * @return this builder for fluent chaining
     */
    public ResponseBodyBuilder block(String tag, Consumer<ResponseBodyBuilder> content) {
        ResponseBodyBuilder childBuilder = new ResponseBodyBuilder();
        content.accept(childBuilder);
        nodes.add(new ResponseBlock(tag, childBuilder.build().nodes()));
        return this;
    }

    /**
     * Build an immutable {@link ResponseBody} from the accumulated nodes.
     *
     * @return a new {@link ResponseBody}
     */
    public ResponseBody build() {
        return new ResponseBody(nodes);
    }
}
