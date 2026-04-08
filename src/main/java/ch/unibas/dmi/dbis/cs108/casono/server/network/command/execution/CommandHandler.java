package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommandHandler<T extends Request> {
    private final List<HandlerCheck> checks = new ArrayList<>();

    protected final void addCheck(HandlerCheck check) {
        checks.add(check);
    }

    public final List<HandlerCheck> getChecks() {
        return Collections.unmodifiableList(checks);
    }

    public void execute(T request) {}
}
