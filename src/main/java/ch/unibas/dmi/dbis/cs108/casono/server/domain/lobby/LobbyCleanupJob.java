package ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import java.time.Duration;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Periodically removes expired empty lobbies and notifies connected sessions. */
public class LobbyCleanupJob implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(LobbyCleanupJob.class);

    private final LobbyManager lobbyManager;
    private final SessionManager sessionManager;
    private final ResponseDispatcher responseDispatcher;
    private final Duration expiryThreshold;

    /**
     * Creates a new cleanup job for expired empty lobbies.
     *
     * @param lobbyManager manager used to find and remove expired lobbies
     * @param sessionManager manager used to resolve connected sessions
     * @param responseDispatcher dispatcher used to notify clients about closed lobbies
     * @param expiryThreshold age threshold that marks an empty lobby as expired
     */
    public LobbyCleanupJob(
            LobbyManager lobbyManager,
            SessionManager sessionManager,
            ResponseDispatcher responseDispatcher,
            Duration expiryThreshold) {
        this.lobbyManager = lobbyManager;
        this.sessionManager = sessionManager;
        this.responseDispatcher = responseDispatcher;
        this.expiryThreshold = expiryThreshold;
    }

    /**
     * Runs one cleanup cycle.
     *
     * <p>The method first fetches all expired empty lobbies. Each lobby is then processed
     * independently so that a failure for one lobby does not stop the remaining cleanups.
     */
    @Override
    public void run() {
        LOGGER.debug("Job started.");
        try {
            List<LobbyId> expired;
            try {
                expired = lobbyManager.findEmptyLobbiesOlderThan(expiryThreshold);
            } catch (Exception e) {
                LOGGER.error("Lobby expiry job failed: could not fetch expired lobbies", e);
                return;
            }

            for (LobbyId lobbyId : expired) {
                try {
                    lobbyManager.removeLobby(lobbyId);
                    broadcastLobbyClosed(lobbyId);
                } catch (RuntimeException e) {
                    LOGGER.warn("Failed to process expired lobby {}", lobbyId.value(), e);
                }
            }
        } finally {
            LOGGER.debug("Job finished.");
        }
    }

    /**
     * Broadcasts a lobby-closed event to all currently connected sessions.
     *
     * @param lobbyId id of the lobby that was closed
     */
    private void broadcastLobbyClosed(LobbyId lobbyId) {
        for (Session session : sessionManager.getAllSessions()) {
            try {
                RequestContext ctx = new RequestContext(session.getId(), 0);
                SuccessResponse event =
                        new SuccessResponse(
                                ctx,
                                ResponseBody.builder()
                                        .param("EVENT", "LOBBY_CLOSED")
                                        .param("LOBBY_ID", lobbyId.value())
                                        .build()) {};
                responseDispatcher.dispatch(event);
            } catch (RuntimeException e) {
                LOGGER.warn(
                        "Failed to notify session {} about closed lobby {}",
                        session.getId().value(),
                        lobbyId.value(),
                        e);
            }
        }
    }
}
