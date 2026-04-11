package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.UserLoggedInCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/**
 * Handler for the `JOIN_LOBBY` command.
 *
 * <p>Resolves the username from the session (requires {@link UserLoggedInCheck}), looks up the
 * target lobby and attempts to add the user. On success an `+OK` response is dispatched; on failure
 * an appropriate {@link ErrorResponse} with one of the error codes `LOBBY_NOT_FOUND`,
 * `USER_NOT_LOGGED_IN` or `LOBBY_FULL_OR_ALREADY_IN` is returned.
 */
public class JoinLobbyHandler extends CommandHandler<JoinLobbyRequest> {
    private final LobbyManager lobbyManager;
    private final UserRegistry userRegistry;

    /**
     * Create a new {@link JoinLobbyHandler}.
     *
     * @param responseDispatcher dispatcher used to send responses back to the client
     * @param lobbyManager manager providing lobby state and operations
     * @param userRegistry registry to resolve session -> user mappings
     */
    public JoinLobbyHandler(
            ResponseDispatcher responseDispatcher,
            LobbyManager lobbyManager,
            UserRegistry userRegistry) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.userRegistry = userRegistry;
        addCheck(new UserLoggedInCheck(userRegistry));
    }

    /**
     * Execute the join-lobby request.
     *
     * @param request the parsed {@link JoinLobbyRequest}
     */
    @Override
    public void execute(JoinLobbyRequest request) {
        LobbyId lid = LobbyId.of(request.getId());
        var lobby = lobbyManager.getLobby(lid);
        if (lobby == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
            return;
        }

        var maybeUser = userRegistry.getBySessionId(request.getContext().sessionId());
        if (maybeUser.isEmpty()) {
            // UserLoggedInCheck should normally prevent this; keep safe fallback
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "USER_NOT_LOGGED_IN",
                            "No user associated with session"));
            return;
        }

        User user = maybeUser.get();
        String username = user.getName();

        boolean ok = lobbyManager.addPlayerToLobby(username, lid);
        if (!ok) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "LOBBY_FULL_OR_ALREADY_IN",
                            "Lobby full or user already in lobby"));
            return;
        }

        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
