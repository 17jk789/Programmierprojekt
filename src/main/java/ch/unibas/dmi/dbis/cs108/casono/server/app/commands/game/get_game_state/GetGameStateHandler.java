package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state;

import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.UserLoggedInCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GamePhase;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/** Handler for GET_GAME_STATE: returns pot, phase, community cards and per-player info. */
public class GetGameStateHandler extends CommandHandler<GetGameStateRequest> {
    private final LobbyManager lobbyManager;
    private final UserRegistry userRegistry;

    public GetGameStateHandler(
            ResponseDispatcher responseDispatcher,
            LobbyManager lobbyManager,
            UserRegistry userRegistry) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.userRegistry = userRegistry;
        addCheck(new UserLoggedInCheck(userRegistry));
    }

    @Override
    public void execute(GetGameStateRequest request) {
        Integer gameId = request.getGameId();
        String username = resolveUsername(request);

        Lobby lobby;
        LobbyId lobbyId;
        if (gameId != null) {
            lobbyId = LobbyId.of(gameId);
            lobby = lobbyManager.getLobby(lobbyId);
            if (lobby == null) {
                responseDispatcher.dispatch(
                        new ErrorResponse(
                                request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
                return;
            }
        } else {
            if (username == null) {
                var maybeUser = userRegistry.getBySessionId(request.getSessionId());
                if (maybeUser.isEmpty()) {
                    responseDispatcher.dispatch(
                            new ErrorResponse(
                                    request.getContext(), "NOT_LOGGED_IN", "User not logged in"));
                    return;
                }
                User u = maybeUser.get();
                username = u.getName();
            }

            lobby = lobbyManager.getLobbyByUsername(username);
            if (lobby == null) {
                responseDispatcher.dispatch(
                        new ErrorResponse(
                                request.getContext(), "NOT_IN_LOBBY", "User not in a lobby"));
                return;
            }
            lobbyId = lobby.getId();
        }

        var game = lobby.getGameController();
        if (game == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "GAME_NOT_STARTED", "Game not started"));
            return;
        }

        if (game.getState().getPhase() == GamePhase.FINISHED) {
            cleanupLobby(lobby, lobbyId);
        }

        responseDispatcher.dispatch(new GetGameStateResponse(request.getContext(), game, username));
    }

    /**
     * Cleans up a lobby by removing all players and resetting the game controller. This is called
     * when the game reaches FINISHED phase.
     */
    private void cleanupLobby(Lobby lobby, LobbyId lobbyId) {
        try {
            // Remove all players from the lobby
            for (String playerName : lobby.getPlayerNames()) {
                lobbyManager.removePlayer(playerName);
            }
            // Reset the game controller so the lobby returns to CREATED state
            lobby.initGame(null);
        } catch (RuntimeException e) {
            // Log silently to avoid disrupting game state response
        }
    }

    private String resolveUsername(GetGameStateRequest request) {
        String username = request.getUsername();
        if (username != null && !username.isBlank()) {
            return username.trim();
        }

        return userRegistry
                .getBySessionId(request.getSessionId())
                .map(User::getName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .orElse(null);
    }
}
