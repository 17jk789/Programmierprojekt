package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Represents a "PING" request sent by a client to check server availability and keep the connection
 * alive.
 */
public class PingRequest extends Request {
    /**
     * Constructs a new PingRequest with the given context.
     *
     * @param context the request context associated with this request
     */
    public PingRequest(RequestContext context) {
        super(context);
    }
}
