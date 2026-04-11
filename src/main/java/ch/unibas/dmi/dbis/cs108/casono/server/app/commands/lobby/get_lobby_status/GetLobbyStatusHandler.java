package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/**
 * Handler for the `GET_LOBBY_STATUS` command.
 *
 * <p>
 * Resolves the target lobby either by the optional `ID` parameter or by
 * `USERNAME`. If both are
 * omitted the handler requires the session to be associated with a logged-in
 * user (see the inline
 * pre-execution check). On success a {@link GetLobbyStatusResponse} is
 * dispatched, otherwise an
 * {@link ErrorResponse} with code `LOBBY_NOT_FOUND` is sent.
 */
public class GetLobbyStatusHandler extends CommandHandler<GetLobbyStatusRequest> {
    private final LobbyManager lobbyManager;
    private final UserRegistry userRegistry;

    public GetLobbyStatusHandler(
            ResponseDispatcher responseDispatcher,
            LobbyManager lobbyManager,
            UserRegistry userRegistry) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.userRegistry = userRegistry;

        // Only require logged-in session if the request does not provide an explicit ID
        // or USERNAME
        addCheck(
                request -> {
                    if (!(request instanceof GetLobbyStatusRequest)) {
                        return Optional.empty();
                    }
                    GetLobbyStatusRequest r = (GetLobbyStatusRequest) request;
                    if (r.getId() != null || r.getUsername() != null) {
                        return Optional.empty();
                    }
                    var maybeUser = userRegistry.getBySessionId(r.getSessionId());
                    if (maybeUser.isPresent()) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            new ErrorResponse(
                                    r.getContext(),
                                    "USER_NOT_LOGGED_IN",
                                    "Request requires an associated logged-in user"));
                });
    }

    @Override
    public void execute(GetLobbyStatusRequest request) {
        // Resolve username from request or session when no explicit ID/USERNAME
        // provided
        String targetUsername = request.getUsername();
        if (targetUsername == null) {
            var maybeUser = userRegistry.getBySessionId(request.getSessionId());
            if (maybeUser.isPresent()) {
                targetUsername = maybeUser.get().getName();
            }
        }

        var lobby = (request.getId() != null)
                ? lobbyManager.getLobby(LobbyId.of(request.getId()))
                : (targetUsername != null
                        ? lobbyManager.getLobbyByUsername(targetUsername)
                        : null);

        if (lobby == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
            return;
        }

        responseDispatcher.dispatch(new GetLobbyStatusResponse(request.getContext(), lobby));
    }
}
