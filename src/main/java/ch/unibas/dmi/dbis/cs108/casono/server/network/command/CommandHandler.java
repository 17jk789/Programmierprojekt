package ch.unibas.dmi.dbis.cs108.casono.server.network.command;

import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.Request;

public interface CommandHandler {
    void execute(Request request);
}
