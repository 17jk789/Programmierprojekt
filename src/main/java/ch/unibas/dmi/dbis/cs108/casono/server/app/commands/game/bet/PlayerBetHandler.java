package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Optional;

/**
 * Handler for the `BET` command.
 *
 * <p>This handler validates the request (amount >= 0), resolves the user and target lobby (by
 * `GAME_ID` when provided or by session username otherwise), checks that a game is running and then
 * forwards the action to the {@code GameController}.
 *
 * <p>On failure the handler dispatches an {@link ErrorResponse} with an appropriate error code
 * (e.g. {@code INVALID_AMOUNT}, {@code NOT_IN_LOBBY}, {@code LOBBY_NOT_FOUND}, {@code
 * GAME_NOT_STARTED}, {@code GAME_ACTION_FAILED}). On success it dispatches an {@link OkResponse}.
 */
public class PlayerBetHandler extends CommandHandler<PlayerBetRequest> {
    private final UserRegistry userRegistry;
    private final LobbyManager lobbyManager;

    /**
     * Creates a new {@code PlayerBetHandler}.
     *
     * @param responseDispatcher dispatcher used to send responses back to the client
     * @param userRegistry registry to resolve users from session ids
     * @param lobbyManager manager used to lookup lobbies and their game controllers
     */
    public PlayerBetHandler(
            ResponseDispatcher responseDispatcher,
            UserRegistry userRegistry,
            LobbyManager lobbyManager) {
        super(responseDispatcher);
        this.userRegistry = userRegistry;
        this.lobbyManager = lobbyManager;
    }

    /**
     * Execute the bet request: validate parameters, resolve lobby and game, and forward the bet
     * action to the game controller. Sends an {@link ErrorResponse} on failure or an {@link
     * OkResponse} on success.
     *
     * @param request the parsed {@link PlayerBetRequest}
     */
    @Override
    public void execute(PlayerBetRequest request) {
        if (request.getAmount() < 0) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "INVALID_AMOUNT", "Amount must be non-negative"));
            return;
        }

        Optional<ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User> opt =
                userRegistry.getBySessionId(request.getSessionId());
        if (opt.isEmpty()) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "NOT_LOGGED_IN", "User not logged in"));
            return;
        }

        String username = opt.get().getName();

        Integer gameId = request.getGameId();
        var lobby =
                (gameId != null)
                        ? lobbyManager.getLobby(LobbyId.of(gameId))
                        : lobbyManager.getLobbyByUsername(username);

        if (lobby == null) {
            if (gameId != null) {
                responseDispatcher.dispatch(
                        new ErrorResponse(
                                request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
            } else {
                responseDispatcher.dispatch(
                        new ErrorResponse(
                                request.getContext(), "NOT_IN_LOBBY", "User not in a lobby"));
            }
            return;
        }

        var game = lobby.getGameController();
        if (game == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "GAME_NOT_STARTED", "Game not started"));
            return;
        }

        try {
            game.playerBet(PlayerId.of(username), request.getAmount());
        } catch (Exception e) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "GAME_ACTION_FAILED", e.getMessage()));
            return;
        }

        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
