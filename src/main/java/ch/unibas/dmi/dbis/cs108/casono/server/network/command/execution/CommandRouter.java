package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import java.util.HashMap;
import java.util.Map;

/** Routes incoming requests to their corresponding {@link CommandHandler} implementations. */
public class CommandRouter {
    private final Map<Class<? extends Request>, CommandHandler<?>> handlers = new HashMap<>();
    private final CommandHandlerExecutor handlerExecutor;

    /**
     * Creates a new CommandRouter with the specified handler executor.
     *
     * @param commandHandlerExecutor to delegate execution of checks and invocation of {@link
     *     CommandHandler} to
     */
    public CommandRouter(CommandHandlerExecutor commandHandlerExecutor) {
        this.handlerExecutor = commandHandlerExecutor;
    }

    /**
     * Registers a {@link CommandHandler} for a specific request type.
     *
     * <p>This method establishes a type-safe mapping between a request class and its handler.
     *
     * <p>Only one handler can be registered per request type; subsequent registrations will
     * overwrite previous ones.
     *
     * @param <T> the type of request handled by the provided handler
     * @param request the request class to associate with the given command handler
     * @param handler the command handler for the given request type
     */
    public <T extends Request> void register(Class<T> request, CommandHandler<T> handler) {
        handlers.put(request, handler);
    }

    /**
     * Executes the appropriate handler for the given request.
     *
     * <p>Looks up the handler registered for the request's class and executes it through the
     * configured {@link CommandHandlerExecutor}.
     *
     * @param request the request to be executed
     * @throws UnknownRequestException if no handler is registered for the request type
     */
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

        handlerExecutor.execute(handler, request);
    }
}
