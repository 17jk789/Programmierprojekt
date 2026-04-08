package ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import java.util.Optional;

@FunctionalInterface
public interface HandlerCheck {
    Optional<Response> check(Request request);
}
