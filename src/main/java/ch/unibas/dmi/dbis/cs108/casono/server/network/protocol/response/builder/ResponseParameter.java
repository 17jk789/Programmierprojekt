package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder;

/**
 * A parameter node stored in a {@link ResponseBody}.
 *
 * <p>Represents a simple key/value pair. Callers can use {@link #rawValue()} to obtain the string
 * representation of the stored value.
 *
 * @param key the parameter name
 * @param value the parameter value
 */
public record ResponseParameter(String key, Object value) implements ResponseNode {
    /**
     * Returns the raw string representation of the stored value. This is a convenience wrapper
     * around {@code Object#toString()} and may throw {@link NullPointerException} if the stored
     * value is {@code null}.
     *
     * @return the value as string
     */
    public String rawValue() {
        return value.toString();
    }
}
