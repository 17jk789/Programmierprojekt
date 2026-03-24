package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

/**
 * Utility responsible for encoding a {@link Response} into a protocol payload string and wrapping
 * it into a {@link PrimitiveResponse} suitable for transmission.
 */
public class ResponseEncoder {
    private static final String INDENT = "\t";
    private static final String NEWLINE = "\n";

    /**
     * Encode a {@link Response} into a {@link PrimitiveResponse} containing the serialized payload
     * string.
     *
     * @param response the response to encode
     * @return a {@link PrimitiveResponse} with encoded payload
     */
    public static PrimitiveResponse encode(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.prefix());

        for (ResponseNode node : response.getBody().nodes()) {
            sb.append(NEWLINE);
            encodeNode(node, sb, 1);
        }

        sb.append(NEWLINE).append("END");

        return new PrimitiveResponse(
                response.getSessionId(), response.getRequestId(), sb.toString());
    }

    /**
     * Internal helper to encode any {@link ResponseNode}.
     *
     * @param node node to encode
     * @param sb string builder to append to
     * @param depth current indentation depth
     */
    private static void encodeNode(ResponseNode node, StringBuilder sb, int depth) {
        if (node instanceof ResponseParameter param) {
            encodeParameter(param, sb, depth);
        } else if (node instanceof ResponseBlock block) {
            encodeBlock(block, sb, depth);
        }
    }

    /**
     * Encode a {@link ResponseParameter} into the string builder.
     *
     * @param param the parameter to encode
     * @param sb the output builder
     * @param depth indentation depth
     */
    private static void encodeParameter(ResponseParameter param, StringBuilder sb, int depth) {
        sb.append(INDENT.repeat(depth));
        sb.append(param.key());
        sb.append("=");
        sb.append(maskIfNeeded(param.value().toString()));
    }

    /**
     * Encode a {@link ResponseBlock}, including its children and terminating with an {@code END}
     * marker.
     *
     * @param block the block to encode
     * @param sb the output builder
     * @param depth current indentation depth
     */
    private static void encodeBlock(ResponseBlock block, StringBuilder sb, int depth) {
        sb.append(INDENT.repeat(depth));
        sb.append(block.tag());

        for (ResponseNode child : block.children()) {
            sb.append(NEWLINE);
            encodeNode(child, sb, depth + 1);
        }

        sb.append(NEWLINE);
        sb.append(INDENT.repeat(depth));
        sb.append("END");
    }

    /**
     * Quote or escape the provided value if it contains whitespace or single quotes so the encoded
     * payload remains parseable.
     *
     * @param value the raw string value
     * @return quoted/escaped value
     */
    private static String maskIfNeeded(String value) {
        if (value.contains(" ") || value.contains("'")) {
            String escaped = value.replace("'", "\\'");
            return "'" + escaped + "'";
        }
        return value;
    }
}
