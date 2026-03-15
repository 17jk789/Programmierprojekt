package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

/**
 * Parser to convert the PrimitiveRequest to a Request and performing checks for required fields and
 * data types
 */
public interface CommandParser {
    /**
     * Parses the provided PrimitiveRequest into a command-specific request
     *
     * @param primitiveRequest
     * @return
     */
    Request parse(PrimitiveRequest primitiveRequest);
}
