package ch.unibas.dmi.dbis.cs108.casono.server.network.command;

import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.Request;

public interface CommandHandler<T extends Request> {
    void execute(T request);
}
