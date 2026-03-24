package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public record RequestContext(SessionId sessionId, int requestId) {}
