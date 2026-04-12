package ch.unibas.dmi.dbis.cs108.casono.server;

import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserCleanupJob;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.NetworkManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandlerExecutor;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandRouter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionDisconnectJob;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Application class for starting the server. */
public class ServerApp {
    private static final int USER_CLEANUP_JOB_DELAY = 0;
    private static final int USER_CLEANUP_JOB_PERIOD = 10;
    private static final int USER_CLEANUP_JOB_RECONNECT_THRESHOLD = 10;
    private static final int SESSION_DISCONNECT_JOB_DELAY = 0;
    private static final int SESSION_DISCONNECT_JOB_PERIOD = 2;
    private static final int SESSION_DISCONNECT_JOB_TIMEOUT = 5;
    private static final int LOBBY_EXPIRY_SECONDS = 30;
    private static final int LOBBY_CLEANUP_INITIAL_DELAY_SECONDS = 5;
    private static final int LOBBY_CLEANUP_PERIOD_SECONDS = 5;

    public static void start(String arg) {
        int port = Integer.parseInt(arg);

        Logger logger = LogManager.getLogger(ServerApp.class);
        logger.info("Starting server at port {}", port);

        EventBus eventBus = new EventBus();
        CommandParserDispatcher dispatcher = new CommandParserDispatcher();
        SessionManager sessionManager = new SessionManager(eventBus, dispatcher);
        ResponseDispatcher responseDispatcher = new ResponseDispatcher(sessionManager);
        CommandHandlerExecutor handlerExecutor = new CommandHandlerExecutor(responseDispatcher);
        CommandRouter router = new CommandRouter(handlerExecutor);

        eventBus.subscribe(DisconnectEvent.class, event -> sessionManager.onDisconnect(event));

        UserRegistry userRegistry = new UserRegistry();
        eventBus.subscribe(
                DisconnectEvent.class, event -> userRegistry.onDisconnect(event.sessionId()));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                new UserCleanupJob(
                        userRegistry, Duration.ofSeconds(USER_CLEANUP_JOB_RECONNECT_THRESHOLD)),
                USER_CLEANUP_JOB_DELAY,
                USER_CLEANUP_JOB_PERIOD,
                TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(
                new SessionDisconnectJob(
                        sessionManager,
                        eventBus,
                        Duration.ofSeconds(SESSION_DISCONNECT_JOB_TIMEOUT)),
                SESSION_DISCONNECT_JOB_DELAY,
                SESSION_DISCONNECT_JOB_PERIOD,
                TimeUnit.SECONDS);

        LobbyManager lobbyManager = new LobbyManager();
        registerCommands(dispatcher, router, responseDispatcher, userRegistry, lobbyManager);

        // Periodic cleanup: remove empty lobbies older than 30s and notify affected
        // users
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        var expired =
                                lobbyManager.findEmptyLobbiesOlderThan(
                                        Duration.ofSeconds(LOBBY_EXPIRY_SECONDS));
                        for (var lid : expired) {
                            // remove lobby from manager first
                            lobbyManager.removeLobby(lid);

                            // broadcast LOBBY_CLOSED event to all connected sessions
                            // (requestId=0)
                            for (Session s : sessionManager.getAllSessions()) {
                                RequestContext ctx = new RequestContext(s.getId(), 0);
                                SuccessResponse ev =
                                        new SuccessResponse(
                                                ctx,
                                                ResponseBody.builder()
                                                        .param("EVENT", "LOBBY_CLOSED")
                                                        .param("LOBBY_ID", lid.value())
                                                        .build()) {};

                                responseDispatcher.dispatch(ev);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Lobby expiry job failed", e);
                    }
                },
                LOBBY_CLEANUP_INITIAL_DELAY_SECONDS,
                LOBBY_CLEANUP_PERIOD_SECONDS,
                TimeUnit.SECONDS);

        NetworkManager networkManager = new NetworkManager(port, sessionManager, router);
        networkManager.start();
    }

    /**
     * Registers command parsers and handlers.
     *
     * @param parserDispatcher the dispatcher responsible for parsing incoming commands
     * @param commandRouter the router that dispatches parsed commands to appropriate handlers
     * @param responseDispatcher the dispatcher responsible for sending responses back to clients
     */
    private static void registerCommands(
            CommandParserDispatcher parserDispatcher,
            CommandRouter commandRouter,
            ResponseDispatcher responseDispatcher,
            UserRegistry userRegistry,
            LobbyManager lobbyManager) {
        parserDispatcher.register("PING", new PingParser());
        commandRouter.register(PingRequest.class, new PingHandler(responseDispatcher));

        parserDispatcher.register("CHECK_USERNAME", new CheckUsernameParser());
        commandRouter.register(
                CheckUsernameRequest.class,
                new CheckUsernameHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("LOGIN", new LoginParser());
        commandRouter.register(
                LoginRequest.class, new LoginHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("LOGOUT", new LogoutParser());
        commandRouter.register(
                LogoutRequest.class, new LogoutHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("SEND_MESSAGE", new SendMessageParser());
        commandRouter.register(
                SendMessageRequest.class, new SendMessageHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("GET_MESSAGE_COUNT", new GetMessageCountParser());
        commandRouter.register(
                GetMessageCountRequest.class,
                new GetMessageCountHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("GET_NEXT_MESSAGE", new GetNextMessageParser());
        commandRouter.register(
                GetNextMessageRequest.class,
                new GetNextMessageHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("LIST_USERS", new ListUsersParser());
        commandRouter.register(
                ListUsersRequest.class, new ListUsersHandler(responseDispatcher, userRegistry));

        // GET_LOBBY_LIST registration
        parserDispatcher.register(
                "GET_LOBBY_LIST",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list
                        .GetLobbyListParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list
                        .GetLobbyListRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby
                                        .get_lobby_list.GetLobbyListRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list
                                .GetLobbyListHandler(responseDispatcher, lobbyManager));

        // GET_GAME_STATE registration
        parserDispatcher.register(
                "GET_GAME_STATE",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state
                        .GetGameStateParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state
                        .GetGameStateRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game
                                        .get_game_state.GetGameStateRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state
                                .GetGameStateHandler(
                                responseDispatcher, lobbyManager, userRegistry));

        // BET registration
        parserDispatcher.register(
                "BET",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet
                                        .PlayerBetRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet
                                .PlayerBetHandler(responseDispatcher, userRegistry, lobbyManager));

        // RAISE registration
        parserDispatcher.register(
                "RAISE",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise
                        .PlayerRaiseParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise.PlayerRaiseRequest
                        .class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise
                                        .PlayerRaiseRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise
                                .PlayerRaiseHandler(
                                responseDispatcher, userRegistry, lobbyManager));

        // CALL registration
        parserDispatcher.register(
                "CALL",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call
                        .PlayerCallParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call.PlayerCallRequest
                        .class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call
                                        .PlayerCallRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call
                                .PlayerCallHandler(responseDispatcher, userRegistry, lobbyManager));

        // FOLD registration
        parserDispatcher.register(
                "FOLD",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold
                        .PlayerFoldParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold.PlayerFoldRequest
                        .class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold
                                        .PlayerFoldRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold
                                .PlayerFoldHandler(responseDispatcher, userRegistry, lobbyManager));

        // GET_LOBBY_STATUS registration
        parserDispatcher.register(
                "GET_LOBBY_STATUS",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status
                        .GetLobbyStatusParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status
                        .GetLobbyStatusRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby
                                        .get_lobby_status.GetLobbyStatusRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby
                                .get_lobby_status.GetLobbyStatusHandler(
                                responseDispatcher, lobbyManager, userRegistry));

        // CREATE_LOBBY registration
        parserDispatcher.register(
                "CREATE_LOBBY",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby
                        .CreateLobbyParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby
                        .CreateLobbyRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby
                                        .create_lobby.CreateLobbyRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby
                                .CreateLobbyHandler(responseDispatcher, lobbyManager));

        // JOIN_LOBBY registration
        parserDispatcher.register(
                "JOIN_LOBBY",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby
                        .JoinLobbyParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby
                        .JoinLobbyRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby
                                        .JoinLobbyRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby
                                .JoinLobbyHandler(responseDispatcher, lobbyManager, userRegistry));

        // START_GAME registration
        parserDispatcher.register(
                "START_GAME",
                new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game
                        .StartGameParser());
        commandRouter.register(
                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game
                        .StartGameRequest.class,
                (ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler<
                                ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game
                                        .StartGameRequest>)
                        new ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game
                                .StartGameHandler(responseDispatcher, lobbyManager, userRegistry));
    }
}
