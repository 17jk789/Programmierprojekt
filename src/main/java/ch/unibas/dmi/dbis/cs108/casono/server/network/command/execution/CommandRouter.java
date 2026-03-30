package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import java.util.HashMap;
import java.util.Map;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;

public class CommandRouter {
    private final Map<Class<? extends Request>, CommandHandler<?>> handlers = new HashMap<>();

    public <T extends Request> void register(Class<T> request, CommandHandler<T> handler) {
        handlers.put(request, handler);
    }

    // Safe, because during registration, it's ensured that the provided CommandHandler only
    // receives requests it can handle.
    @SuppressWarnings("unchecked")
    public void execute(Request request) {
        CommandHandler<Request> handler =
                (CommandHandler<Request>) handlers.get(request.getClass());

        if (handler == null) {
            String requestName = request.getClass().toString();
            throw new UnknownRequestException(
                    "Unable to execute request " + requestName + ". Type unknown", requestName);
        }
        handler.execute(request);
    }
}
