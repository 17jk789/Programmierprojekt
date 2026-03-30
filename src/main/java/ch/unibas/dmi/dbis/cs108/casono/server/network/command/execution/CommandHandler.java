package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;

public interface CommandHandler<T extends Request> {
    void execute(T request);
}
