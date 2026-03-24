package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

public record ResponseParameter(String key, Object value) implements ResponseNode {
    public String rawValue() {
        return value.toString();
    }
}
