package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import java.util.HashMap;
import java.util.Map;

/** Dispatcher all CommandParser are registered in */
public class CommandParserDispatcher {
    private final Map<String, CommandParser> parsers = new HashMap<>();

    /**
     * Register a new CommandParser
     *
     * @param command
     * @param parser the parser class
     */
    public void register(String command, CommandParser parser) {
        parsers.put(command, parser);
    }

    /**
     * Parses the PrimitiveRequest into a Request using the appropriate CommandParser
     *
     * @param primitiveRequest the PrimitiveRequest to parse
     * @return the parsed Request object
     */
    public Request parse(PrimitiveRequest primitiveRequest) {
        String command = primitiveRequest.command();
        CommandParser parser = parsers.get(command);

        if (parser == null) {
            throw new UnknownCommandException(command);
        }

        return parser.parse(primitiveRequest);
    }
}
