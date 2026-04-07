package ch.unibas.dmi.dbis.cs108.casono.server;

import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick.CheckUsernameRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.login.LoginRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.logout.LogoutRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_message_count.GetMessageCountRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.get_next_message.GetNextMessageRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.ping.PingRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageParser;
import ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message.SendMessageRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserCleanupJob;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.NetworkManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandlerExecutor;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandRouter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionDisconnectJob;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Application class for starting the server. */
public class ServerApp {
    private static final int USER_CLEANUP_JOB_DELAY = 0;
    private static final int USER_CLEANUP_JOB_PERIOD = 10;
    private static final int USER_CLEANUP_JOB_RECONNECT_THRESHOLD = 10;
    private static final int SESSION_DISCONNECT_JOB_DELAY = 0;
    private static final int SESSION_DISCONNECT_JOB_PERIOD = 2;
    private static final int SESSION_DISCONNECT_JOB_TIMEOUT = 30;

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

        registerCommands(dispatcher, router, responseDispatcher, userRegistry);

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
            UserRegistry userRegistry) {
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
                SendMessageRequest.class,
                new SendMessageHandler(responseDispatcher, userRegistry)
        );


        parserDispatcher.register("GET_MESSAGE_COUNT", new GetMessageCountParser());
        commandRouter.register(
                GetMessageCountRequest.class,
                new GetMessageCountHandler(responseDispatcher, userRegistry)
        );


        parserDispatcher.register("GET_NEXT_MESSAGE", new GetNextMessageParser());
        commandRouter.register(
                GetNextMessageRequest.class,
                new GetNextMessageHandler(responseDispatcher, userRegistry)
        );
    }
}
