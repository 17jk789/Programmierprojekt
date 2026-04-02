package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/**
 * Immutable transport record representing an encoded response ready for delivery to a session.
 *
 * @param sessionId the target session id
 * @param requestId the originating request id
 * @param payload the serialized response payload
 */
public record PrimitiveResponse(SessionId sessionId, int requestId, String payload) {}
