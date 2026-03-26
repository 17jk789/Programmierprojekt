package ch.unibas.dmi.dbis.cs108.casono.server.network.command;

import java.util.HashMap;
import java.util.Map;

import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.Request;

public class CommandRouter {
    private final Map<Class<? extends Request>, CommandHandler> handlers = new HashMap<>();

    public void register(Class<? extends Request> request, CommandHandler handler) {
        handlers.put(request, handler);
    }

    public void execute(Request request) {
        CommandHandler handler = handlers.get(request.getClass());

        if (handler == null) {
            String requestName = request.getClass().toString();
            throw new UnknownRequestException("Unable to execute request " + requestName + ". Type unknown", requestName);
        }
        handler.execute(request);
    }
}
