package ch.unibas.dmi.dbis.cs108.casono.server.network.events;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public record DisconnectEvent(SessionId sessionId) implements Event {}
