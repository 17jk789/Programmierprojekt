package ch.unibas.dmi.dbis.cs108.casono.server.network.request.accessor;

/**
 * Functional parser interface used to convert a raw string parameter into a target type.
 *
 * @param <T> target type produced by the parser
 */
@FunctionalInterface
interface ThrowingParser<T> {
    /**
     * Parses the provided raw parameter value.
     *
     * @param value raw parameter value
     * @return parsed value
     * @throws Exception if the value cannot be parsed
     */
    T parse(String value) throws Exception;
}
