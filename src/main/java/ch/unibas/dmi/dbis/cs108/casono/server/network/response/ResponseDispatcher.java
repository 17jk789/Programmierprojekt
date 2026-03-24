package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;

/**
 * Helper that dispatches {@link Response} instances to the corresponding {@link Session} by
 * encoding them and enqueuing the resulting {@link PrimitiveResponse} into the session's response
 * queue.
 */
public class ResponseDispatcher {
    private final SessionManager sessionManager;

    /**
     * Create a dispatcher bound to a {@link SessionManager}.
     *
     * @param sessionManager manager used to resolve sessions
     */
    public ResponseDispatcher(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Encode the given {@link Response} and enqueue the resulting {@link PrimitiveResponse} into
     * the target session's response queue.
     *
     * @param response the response to dispatch
     * @throws InterruptedException if the thread is interrupted while waiting to enqueue the
     *     primitive response
     */
    public void dispatch(Response response) throws InterruptedException {
        PrimitiveResponse primitiveResponse = ResponseEncoder.encode(response);
        Session session = sessionManager.getSessionById(response.getSessionId());
        session.getResponseQueue().put(primitiveResponse);
    }
}
