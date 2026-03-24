package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

public class ResponseEncoder {
    private static final String INDENT = "\t";
    private static final String NEWLINE = "\n";

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

    private static void encodeNode(ResponseNode node, StringBuilder sb, int depth) {
        if (node instanceof ResponseParameter param) {
            encodeParameter(param, sb, depth);
        } else if (node instanceof ResponseBlock block) {
            encodeBlock(block, sb, depth);
        }
    }

    private static void encodeParameter(ResponseParameter param, StringBuilder sb, int depth) {
        sb.append(INDENT.repeat(depth));
        sb.append(param.key());
        sb.append("=");
        sb.append(maskIfNeeded(param.value().toString()));
    }

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

    private static String maskIfNeeded(String value) {
        if (value.contains(" ")) {
            return "'" + value + "'";
        }
        return value;
    }
}
