package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/**
 * Immutable context for a network request.
 *
 * <p>Contains the originating session's identifier and the request's id. Later used to create
 * response.
 *
 * @param sessionId the identifier of the session that initiated the request
 * @param requestId the request's numeric id within the session
 */
public record RequestContext(SessionId sessionId, int requestId) {}
