package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.leave_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.UserLoggedInCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handler for the `LEAVE_LOBBY` command.
 *
 * <p>Resolves the username from the session (requires {@link UserLoggedInCheck}), looks up the
 * target lobby and marks the user as absent. The user can rejoin later without losing their slot.
 * On success an `+OK` response is dispatched; on failure an appropriate {@link ErrorResponse} is
 * returned.
 */
public class LeaveLobbyHandler extends CommandHandler<LeaveLobbyRequest> {
    private final LobbyManager lobbyManager;
    private final UserRegistry userRegistry;
    private static final Logger LOGGER =
            LogManager.getLogger(LeaveLobbyHandler.class.getSimpleName());

    /**
     * Create a new {@link LeaveLobbyHandler}.
     *
     * @param responseDispatcher dispatcher used to send responses back to the client
     * @param lobbyManager manager providing lobby state and operations
     * @param userRegistry registry to resolve session -> user mappings
     */
    public LeaveLobbyHandler(
            ResponseDispatcher responseDispatcher,
            LobbyManager lobbyManager,
            UserRegistry userRegistry) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.userRegistry = userRegistry;
        addCheck(new UserLoggedInCheck(userRegistry));
    }

    /**
     * Execute the leave-lobby request. Marks the player as absent in the lobby.
     *
     * @param request the parsed {@link LeaveLobbyRequest}
     */
    @Override
    public void execute(LeaveLobbyRequest request) {
        LOGGER.info(
                "LEAVE_LOBBY request: session={}, lobbyId={}",
                request.getContext().sessionId(),
                request.getId());
        LobbyId lid = LobbyId.of(request.getId());
        var lobby = lobbyManager.getLobby(lid);
        if (lobby == null) {
            LOGGER.warn(
                    "LEAVE_LOBBY: Lobby {} not found (session={})",
                    request.getId(),
                    request.getContext().sessionId());
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
            return;
        }

        // Only allow leaving if game is running
        if (lobby.getGameController() == null) {
            LOGGER.warn(
                    "LEAVE_LOBBY: Game not running in lobby {} (session={})",
                    request.getId(),
                    request.getContext().sessionId());
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "GAME_NOT_RUNNING",
                            "Can only leave a lobby while a game is running"));
            return;
        }

        var maybeUser = userRegistry.getBySessionId(request.getContext().sessionId());
        if (maybeUser.isEmpty()) {
            LOGGER.warn(
                    "LEAVE_LOBBY: No user associated with session {}",
                    request.getContext().sessionId());
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "USER_NOT_LOGGED_IN",
                            "No user associated with session"));
            return;
        }

        User user = maybeUser.get();
        String username = user.getName();

        boolean ok = lobbyManager.leavePlayerFromLobby(username, lid);
        if (!ok) {
            LOGGER.warn(
                    "LEAVE_LOBBY: User '{}' failed to leave lobby {} (not in lobby or not active)",
                    username,
                    request.getId());
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "NOT_IN_LOBBY",
                            "You are not in this lobby or not actively playing"));
            return;
        }

        LOGGER.info("User '{}' left lobby {} (marked absent)", username, request.getId());
        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
