package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import java.util.Optional;
import java.util.regex.Pattern;

/** Handles CHANGE_USERNAME requests for already logged-in users. */
public class ChangeUsernameHandler extends CommandHandler<ChangeUsernameRequest> {
    private static final Pattern VALID_USERNAME = Pattern.compile("[a-zA-Z0-9_-]+");
    private final UserRegistry userRegistry;
    private final LobbyManager lobbyManager;
    private final SessionManager sessionManager;

    /**
     * @param responseDispatcher dispatcher used for responses
     * @param userRegistry registry containing all users
     * @param lobbyManager lobby manager used to keep lobby/game mappings in sync
     * @param sessionManager session manager used to broadcast rename events
     */
    public ChangeUsernameHandler(
            ResponseDispatcher responseDispatcher,
            UserRegistry userRegistry,
            LobbyManager lobbyManager,
            SessionManager sessionManager) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
        this.lobbyManager = lobbyManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void execute(ChangeUsernameRequest request) {
        Optional<User> user = userRegistry.getBySessionId(request.getSessionId());
        if (user.isEmpty()) {
            dispatchError(
                    request.getContext(),
                    "USER_NOT_LOGGED_IN",
                    "This session is not associated with an active user.");
            return;
        }

        String newUsername = validateUsername(request);
        if (newUsername == null) {
            return;
        }

        User currentUser = user.get();
        String oldUsername = currentUser.getName();

        if (isUsernameChangeBlocked(request.getContext(), oldUsername)) {
            return;
        }

        boolean changed = userRegistry.changeUsername(currentUser.getId(), newUsername);
        if (!changed) {
            dispatchError(
                    request.getContext(),
                    "USERNAME_TAKEN",
                    "The requested username is already taken.");
            return;
        }

        boolean lobbySynced =
                lobbyManager == null || lobbyManager.renamePlayer(oldUsername, newUsername);
        if (!lobbySynced) {
            userRegistry.changeUsername(currentUser.getId(), oldUsername);
            dispatchError(
                    request.getContext(),
                    "RENAME_CONFLICT",
                    "Could not update username in current lobby/game state.");
            return;
        }

        responseDispatcher.dispatch(
                new ChangeUsernameResponse(
                        request.getContext(), currentUser.getName(), currentUser.getId()));

        broadcastUsernameChanged(oldUsername, currentUser.getName());
    }

    private void broadcastUsernameChanged(String oldUsername, String newUsername) {
        if (sessionManager == null) {
            return;
        }

        for (Session session : sessionManager.getAllSessions()) {
            RequestContext ctx = new RequestContext(session.getId(), 0);
            SuccessResponse ev =
                    new SuccessResponse(
                            ctx,
                            new ResponseBodyBuilder()
                                    .param("EVENT", "USERNAME_CHANGED")
                                    .param("OLD_USERNAME", oldUsername)
                                    .param("NEW_USERNAME", newUsername)
                                    .build()) {};
            responseDispatcher.dispatch(ev);
        }
    }

    private String validateUsername(ChangeUsernameRequest request) {
        String newUsername = request.getUsername() == null ? "" : request.getUsername().trim();
        if (newUsername.isEmpty() || !VALID_USERNAME.matcher(newUsername).matches()) {
            dispatchError(
                    request.getContext(),
                    "INVALID_USERNAME",
                    "Only letters, numbers, '_' and '-' are allowed.");
            return null;
        }
        return newUsername;
    }

    private boolean isUsernameChangeBlocked(RequestContext context, String oldUsername) {
        if (lobbyManager == null) {
            return false;
        }

        var lobby = lobbyManager.getLobbyByUsername(oldUsername);
        boolean gameRunning =
                lobby != null
                        && lobby.getGameController() != null
                        && lobby.getGameController().getState().getPhase() != GamePhase.FINISHED;
        if (gameRunning && lobby.isPlayerActive(oldUsername)) {
            dispatchError(
                    context,
                    "CANNOT_CHANGE_USERNAME_DURING_LOBBY",
                    "Cannot change username while actively playing in a lobby. "
                            + "Wait until the game ends.");
            return true;
        }
        return false;
    }

    private void dispatchError(RequestContext context, String code, String message) {
        responseDispatcher.dispatch(new ErrorResponse(context, code, message));
    }
}
