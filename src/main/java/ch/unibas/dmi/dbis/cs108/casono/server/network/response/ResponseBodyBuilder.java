package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ResponseBodyBuilder {
    private final List<ResponseNode> nodes = new ArrayList<>();

    public ResponseBodyBuilder param(String key, Object value) {
        nodes.add(new ResponseParameter(key, value));
        return this;
    }

    public ResponseBodyBuilder block(String tag, Consumer<ResponseBodyBuilder> content) {
        ResponseBodyBuilder childBuilder = new ResponseBodyBuilder();
        content.accept(childBuilder);
        nodes.add(new ResponseBlock(tag, childBuilder.build().nodes()));
        return this;
    }

    public ResponseBody build() {
        return new ResponseBody(nodes);
    }
}
