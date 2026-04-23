package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold;

import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.GameLobbyExistsCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.UserLoggedInCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.GameController;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/**
 * Handler for the `FOLD` command.
 *
 * <p>This handler validates the session, resolves the lobby (by `GAME_ID` when provided or by
 * session username otherwise), checks for a running game and forwards the fold action to the {@code
 * GameController}.
 */
public class PlayerFoldHandler extends CommandHandler<PlayerFoldRequest> {
    private final UserRegistry userRegistry;
    private final LobbyManager lobbyManager;

    /**
     * Creates a new {@code PlayerFoldHandler}.
     *
     * @param responseDispatcher dispatcher used to send responses back to the client
     * @param userRegistry registry to resolve users from session ids
     * @param lobbyManager manager used to lookup lobbies and their game controllers
     */
    public PlayerFoldHandler(
            ResponseDispatcher responseDispatcher,
            UserRegistry userRegistry,
            LobbyManager lobbyManager) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
        this.lobbyManager = lobbyManager;
        addCheck(new UserLoggedInCheck(userRegistry));
        addCheck(new GameLobbyExistsCheck(userRegistry, lobbyManager));
    }

    /**
     * Execute the fold request: resolve lobby by `GAME_ID` if present, otherwise by session user,
     * verify game is running, and forward the fold to the game controller.
     *
     * @param request the parsed {@link PlayerFoldRequest}
     */
    @Override
    public void execute(PlayerFoldRequest request) {
        Optional<User> opt = userRegistry.getBySessionId(request.getSessionId());
        if (opt.isEmpty()) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "NOT_LOGGED_IN", "User not logged in"));
            return;
        }

        String username = opt.get().getName();

        Integer gameId = request.getGameId();
        Lobby lobby;
        if (gameId != null) {
            lobby = lobbyManager.getLobby(LobbyId.of(gameId));
        } else {
            lobby = lobbyManager.getLobbyByUsername(username);
        }

        GameController game = lobby.getGameController();
        if (game == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "GAME_NOT_STARTED", "Game not started"));
            return;
        }

        try {
            game.playerFold(PlayerId.of(username));
        } catch (RuntimeException e) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "GAME_ACTION_FAILED", e.getMessage()));
            return;
        }

        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
