package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides typed access to a request's parameters by indexing them by key.
 *
 * <p>Supports required and optional lookups, with optional conversion from {@link String} values to
 * domain-specific types via parser functions.
 */
public class RequestParameterAccessor {
    private final Map<String, String> index;

    /**
     * Creates an accessor
     *
     * @param parameters to use
     */
    public RequestParameterAccessor(List<RequestParameter> parameters) {
        this.index =
                parameters.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        RequestParameter::key, RequestParameter::value));
    }

    /**
     * Returns the raw value for a required parameter key.
     *
     * @param key parameter key to look up
     * @return raw parameter value
     * @throws MissingParameterException if no parameter with the given key exists
     */
    public String require(String key) throws MissingParameterException {
        String value = index.get(key);
        if (value == null) {
            throw new MissingParameterException(
                    "Required parameter with key '" + key + "' is missing.", key);
        }
        return value;
    }

    /**
     * Returns a parsed value for a required parameter key.
     *
     * @param key parameter key to look up
     * @param parser parser used to convert the raw value
     * @param <T> target type returned by the parser
     * @return parsed parameter value
     * @throws MissingParameterException if no parameter with the given key exists
     * @throws ParameterParseException if parsing the raw value fails
     */
    public <T> T require(String key, ThrowingParser<T> parser)
            throws MissingParameterException, ParameterParseException {
        String value = require(key);
        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new ParameterParseException(
                    "Error while parsing '" + key + "' with specified parser", key, e);
        }
    }

    /**
     * Returns the raw value for a parameter key or the provided default value if missing.
     *
     * @param key parameter key to look up
     * @param defaultValue value returned when the key does not exist
     * @return found parameter value or {@code defaultValue} if absent
     */
    public String optional(String key, String defaultValue) {
        String value = index.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns a parsed value for a parameter key or the provided default value if missing.
     *
     * @param key parameter key to look up
     * @param defaultValue value returned when the key does not exist
     * @param parser parser used to convert the raw value
     * @param <T> target type returned by the parser
     * @return parsed parameter value or {@code defaultValue} if absent
     * @throws ParameterParseException if parsing the raw value fails
     */
    public <T> T optional(String key, T defaultValue, ThrowingParser<T> parser)
            throws ParameterParseException {
        String value = index.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new ParameterParseException(
                    "Error while parsing '" + key + "' with specified parser", key, e);
        }
    }

    /**
     * Checks whether a parameter with the given key exists.
     *
     * @param key parameter key to check
     * @return {@code true} if the key exists, otherwise {@code false}
     */
    public boolean containsKey(String key) {
        return index.containsKey(key);
    }
}
