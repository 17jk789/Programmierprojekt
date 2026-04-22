package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game;

import ch.unibas.dmi.dbis.cs108.casono.server.app.checks.UserLoggedInCheck;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.GameController;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.GameEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.RoundManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.engine.TurnManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.player.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.rules.RuleEngine;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.game.state.GameState;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Handler for START_GAME: creates a GameEngine+GameController for the lobby and starts the game.
 */
public class StartGameHandler extends CommandHandler<StartGameRequest> {
    private final LobbyManager lobbyManager;
    private final UserRegistry userRegistry;
    private static final int DEFAULT_START_CHIPS = 20000;

    public StartGameHandler(
            ResponseDispatcher responseDispatcher,
            LobbyManager lobbyManager,
            UserRegistry userRegistry) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.userRegistry = userRegistry;
        addCheck(new UserLoggedInCheck(userRegistry));
    }

    @Override
    public void execute(StartGameRequest request) {
        Optional<User> opt = userRegistry.getBySessionId(request.getSessionId());
        if (opt.isEmpty()) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "USER_NOT_LOGGED_IN", "User not logged in"));
            return;
        }

        String username = opt.get().getName();

        // Resolve lobby by provided ID (required)
        LobbyId lid = LobbyId.of(request.getId());
        Lobby lobby = lobbyManager.getLobby(lid);
        if (lobby == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(request.getContext(), "LOBBY_NOT_FOUND", "Lobby not found"));
            return;
        }

        // Ensure requester is part of the lobby
        if (!lobby.getPlayerNames().contains(username)) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(), "NOT_IN_LOBBY", "User not in specified lobby"));
            return;
        }

        // Prevent double-start
        if (lobby.getGameController() != null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "ALREADY_STARTED",
                            "Game has already been started for this lobby"));
            return;
        }

        // Basic player count check
        if (lobby.getPlayerNames().size() < 2) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "NOT_ENOUGH_PLAYERS",
                            "Not enough players to start the game"));
            return;
        }

        // Create engine + controller
        GameState state = new GameState();
        GameEngine engine =
                new GameEngine(
                        state,
                        new RuleEngine(new ArrayList<>()),
                        new RoundManager(),
                        new TurnManager());
        GameController game = new GameController(engine);

        // Add all players from lobby
        for (String playerName : lobby.getPlayerNames()) {
            game.addPlayer(PlayerId.of(playerName), DEFAULT_START_CHIPS);
        }

        // Start the game and attach to lobby
        game.startGame();
        lobby.initGame(game);

        responseDispatcher.dispatch(
                new StartGameResponse(
                        request.getContext(), lid.value(), "Game started successfully"));
    }
}
