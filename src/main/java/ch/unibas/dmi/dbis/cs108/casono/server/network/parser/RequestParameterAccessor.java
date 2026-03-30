package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParameterAccessor {
    private final Map<String, String> index;

    public RequestParameterAccessor(List<Parameter> parameters) {
        this.index = parameters.stream()
            .collect(Collectors.toUnmodifiableMap(
                Parameter::key,
                Parameter::value
            ));
    }

    public String require(String key) throws MissingParameterException {
        String value = index.get(key);
        if (value == null) {
            throw new MissingParameterException("Required parameter with key '" + key + "' is missing.", key);
        }
        return value;
    }

    public <T> T require(String key, ThrowingParser<T> parser) throws MissingParameterException, ParameterParseException {
        String value = require(key);
        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new ParameterParseException("Error while parsing with specified parser", e);
        }
    }

    public String optional(String key, String defaultValue) {
        String value = index.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public <T> T optional(String key, T defaultValue, ThrowingParser<T> parser) throws ParameterParseException {
        String value = index.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new ParameterParseException("Error while parsing with specified parser", e);
        }
    }

    public boolean containsKey(String key) {
        return index.containsKey(key);
    }
}
