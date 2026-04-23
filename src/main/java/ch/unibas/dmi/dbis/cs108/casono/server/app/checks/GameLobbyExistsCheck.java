package ch.unibas.dmi.dbis.cs108.casono.server.app.checks;

import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call.PlayerCallRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold.PlayerFoldRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise.PlayerRaiseRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.checks.HandlerCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import java.util.Optional;

/**
 * Checks whether a target lobby exists for game action commands.
 *
 * <p>Supported commands are BET, CALL, FOLD and RAISE. If a request specifies a GAME_ID, the check
 * validates that this lobby exists. Otherwise, it validates that the logged-in user is in a lobby.
 */
public class GameLobbyExistsCheck implements HandlerCheck {
    private final UserRegistry userRegistry;
    private final LobbyManager lobbyManager;

    public GameLobbyExistsCheck(UserRegistry userRegistry, LobbyManager lobbyManager) {
        this.userRegistry = userRegistry;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public Optional<Response> check(Request request) {
        Integer gameId = extractGameId(request);
        User user = userRegistry.getBySessionId(request.getSessionId()).get();

        Lobby lobby;
        if (gameId != null) {
            lobby = lobbyManager.getLobby(LobbyId.of(gameId));
        } else {
            lobby = lobbyManager.getLobbyByUsername(user.getName());
        }

        if (lobby != null) {
            return Optional.empty();
        }

        if (gameId != null) {
            return Optional.of(
                    new ErrorResponse(request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
        }

        return Optional.of(
                new ErrorResponse(request.getContext(), "NOT_IN_LOBBY", "User not in a lobby"));
    }

    private Integer extractGameId(Request request) {
        if (request instanceof PlayerBetRequest) {
            return ((PlayerBetRequest) request).getGameId();
        }
        if (request instanceof PlayerCallRequest) {
            return ((PlayerCallRequest) request).getGameId();
        }
        if (request instanceof PlayerFoldRequest) {
            return ((PlayerFoldRequest) request).getGameId();
        }
        if (request instanceof PlayerRaiseRequest) {
            return ((PlayerRaiseRequest) request).getGameId();
        }

        throw new IllegalStateException(
                "GameLobbyExistsCheck is only supported for BET, CALL, FOLD and RAISE requests");
    }
}
