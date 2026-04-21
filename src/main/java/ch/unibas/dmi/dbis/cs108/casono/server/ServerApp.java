package ch.unibas.dmi.dbis.cs108.casono.server;

import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username.ChangeUsernameHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username.ChangeUsernameParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.change_username.ChangeUsernameRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet.PlayerBetRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call.PlayerCallHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call.PlayerCallParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.call.PlayerCallRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold.PlayerFoldHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold.PlayerFoldParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.fold.PlayerFoldRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state.GetGameStateHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state.GetGameStateParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.get_game_state.GetGameStateRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise.PlayerRaiseHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise.PlayerRaiseParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise.PlayerRaiseRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores.ClearHighscoresHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores.ClearHighscoresParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores.ClearHighscoresRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores.GetHighscoresHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores.GetHighscoresParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores.GetHighscoresRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users.ListUsersRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby.CreateLobbyHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby.CreateLobbyParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby.CreateLobbyRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list.GetLobbyListHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list.GetLobbyListParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list.GetLobbyListRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status.GetLobbyStatusHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status.GetLobbyStatusParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_status.GetLobbyStatusRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby.JoinLobbyHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby.JoinLobbyParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.join_lobby.JoinLobbyRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game.StartGameHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game.StartGameParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.start_game.StartGameRequest;
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
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
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
    private static final int SESSION_DISCONNECT_JOB_TIMEOUT = 30;
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
        registerCommands(
                dispatcher,
                router,
                responseDispatcher,
                userRegistry,
                new CommandContext(lobbyManager, sessionManager));

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

    private static record CommandContext(
            LobbyManager lobbyManager, SessionManager sessionManager) {}

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
            CommandContext context) {
        parserDispatcher.register("PING", new PingParser());
        commandRouter.register(PingRequest.class, new PingHandler(responseDispatcher));

        parserDispatcher.register("CHECK_USERNAME", new CheckUsernameParser());
        commandRouter.register(
                CheckUsernameRequest.class,
                new CheckUsernameHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("LOGIN", new LoginParser());
        commandRouter.register(
                LoginRequest.class, new LoginHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("CHANGE_USERNAME", new ChangeUsernameParser());
        commandRouter.register(
                ChangeUsernameRequest.class,
                new ChangeUsernameHandler(
                        responseDispatcher,
                        userRegistry,
                        context.lobbyManager(),
                        context.sessionManager()));

        parserDispatcher.register("LOGOUT", new LogoutParser());
        commandRouter.register(
                LogoutRequest.class, new LogoutHandler(responseDispatcher, userRegistry));

        parserDispatcher.register("SEND_MESSAGE", new SendMessageParser());
        commandRouter.register(
                SendMessageRequest.class,
                new SendMessageHandler(responseDispatcher, userRegistry, context.lobbyManager()));

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
        parserDispatcher.register("GET_LOBBY_LIST", new GetLobbyListParser());
        commandRouter.register(
                GetLobbyListRequest.class,
                (CommandHandler<GetLobbyListRequest>)
                        new GetLobbyListHandler(responseDispatcher, context.lobbyManager()));

        // GET_GAME_STATE registration
        parserDispatcher.register("GET_GAME_STATE", new GetGameStateParser());
        commandRouter.register(
                GetGameStateRequest.class,
                (CommandHandler<GetGameStateRequest>)
                        new GetGameStateHandler(
                                responseDispatcher, context.lobbyManager(), userRegistry));

        parserDispatcher.register("GET_HIGHSCORES", new GetHighscoresParser());
        commandRouter.register(
                GetHighscoresRequest.class,
                (CommandHandler<GetHighscoresRequest>)
                        new GetHighscoresHandler(responseDispatcher));

        parserDispatcher.register("CLEAR_HIGHSCORES", new ClearHighscoresParser());
        commandRouter.register(
                ClearHighscoresRequest.class,
                (CommandHandler<ClearHighscoresRequest>)
                        new ClearHighscoresHandler(responseDispatcher));

        // BET registration
        parserDispatcher.register("BET", new PlayerBetParser());
        commandRouter.register(
                PlayerBetRequest.class,
                (CommandHandler<PlayerBetRequest>)
                        new PlayerBetHandler(
                                responseDispatcher, userRegistry, context.lobbyManager()));

        // RAISE registration
        parserDispatcher.register("RAISE", new PlayerRaiseParser());
        commandRouter.register(
                PlayerRaiseRequest.class,
                (CommandHandler<PlayerRaiseRequest>)
                        new PlayerRaiseHandler(
                                responseDispatcher, userRegistry, context.lobbyManager()));

        // CALL registration
        parserDispatcher.register("CALL", new PlayerCallParser());
        commandRouter.register(
                PlayerCallRequest.class,
                (CommandHandler<PlayerCallRequest>)
                        new PlayerCallHandler(
                                responseDispatcher, userRegistry, context.lobbyManager()));

        // FOLD registration
        parserDispatcher.register("FOLD", new PlayerFoldParser());
        commandRouter.register(
                PlayerFoldRequest.class,
                (CommandHandler<PlayerFoldRequest>)
                        new PlayerFoldHandler(
                                responseDispatcher, userRegistry, context.lobbyManager()));

        // GET_LOBBY_STATUS registration
        parserDispatcher.register("GET_LOBBY_STATUS", new GetLobbyStatusParser());
        commandRouter.register(
                GetLobbyStatusRequest.class,
                (CommandHandler<GetLobbyStatusRequest>)
                        new GetLobbyStatusHandler(
                                responseDispatcher, context.lobbyManager(), userRegistry));

        // CREATE_LOBBY registration
        parserDispatcher.register("CREATE_LOBBY", new CreateLobbyParser());
        commandRouter.register(
                CreateLobbyRequest.class,
                (CommandHandler<CreateLobbyRequest>)
                        new CreateLobbyHandler(
                                responseDispatcher,
                                context.lobbyManager(),
                                context.sessionManager()));

        // JOIN_LOBBY registration
        parserDispatcher.register("JOIN_LOBBY", new JoinLobbyParser());
        commandRouter.register(
                JoinLobbyRequest.class,
                (CommandHandler<JoinLobbyRequest>)
                        new JoinLobbyHandler(
                                responseDispatcher, context.lobbyManager(), userRegistry));

        // START_GAME registration
        parserDispatcher.register("START_GAME", new StartGameParser());
        commandRouter.register(
                StartGameRequest.class,
                (CommandHandler<StartGameRequest>)
                        new StartGameHandler(
                                responseDispatcher, context.lobbyManager(), userRegistry));
    }
}
