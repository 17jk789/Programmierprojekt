package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;

public class ResponseDispatcher {
    private final SessionManager sessionManager;

    public ResponseDispatcher(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void dispatch(Response response) throws InterruptedException {
        PrimitiveResponse primitiveResponse = ResponseEncoder.encode(response);
        Session session = sessionManager.getSessionById(response.getSessionId());
        session.getResponseQueue().put(primitiveResponse);
    }
}
