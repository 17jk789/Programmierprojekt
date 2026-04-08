package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for handling requests of a specific type.
 *
 * @param <T> the type of request this handler processes, must extend {@link Request}
 */
public abstract class CommandHandler<T extends Request> {
    private final List<HandlerCheck> checks = new ArrayList<>();
    protected final ResponseDispatcher responseDispatcher;

    public CommandHandler(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    /**
     * Adds a handler check to be performed before request execution.
     *
     * <p>The {@code execute} Method is only invoked, if all specified checks passed.
     *
     * @param check the {@link HandlerCheck} to add
     */
    protected final void addCheck(HandlerCheck check) {
        checks.add(check);
    }

    /**
     * Returns an unmodifiable list of all registered handler checks.
     *
     * @return an unmodifiable list of {@link HandlerCheck} objects
     */
    public final List<HandlerCheck> getChecks() {
        return Collections.unmodifiableList(checks);
    }

    /**
     * Executes the request.
     *
     * <p>Subclasses should override this method to implement their specific request handling logic.
     *
     * @param request the request to execute, of type T
     */
    public void execute(T request) {}
}
