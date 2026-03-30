package ch.unibas.dmi.dbis.cs108.casono.server.network.response.builder;

import java.util.List;

/**
 * A block node that contains a tag and a list of child {@link ResponseNode} elements. Blocks can be
 * nested to build hierarchical response bodies.
 *
 * @param tag the block tag
 * @param children the child nodes contained in this block
 */
public record ResponseBlock(String tag, List<ResponseNode> children) implements ResponseNode {}
