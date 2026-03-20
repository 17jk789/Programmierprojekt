package ch.unibas.dmi.dbis.cs108.casono.server;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserCleanupJob;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.NetworkManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Application class for starting the server. */
public class ServerApp {
    public static final int USER_CLEANUP_JOB_DELAY = 0;
    public static final int USER_CLEANUP_JOB_PERIOD = 10;
    public static final int USER_CLEANUP_JOB_RECONNECT_THRESHOLD = 10;

    public static void start(String arg) {
        int port = Integer.parseInt(arg);

        Logger logger = LogManager.getLogger(ServerApp.class);
        logger.info("Starting server at port {}", port);

        EventBus eventBus = new EventBus();
        SessionManager sessionManager = new SessionManager(eventBus);
        eventBus.subscribe(
                DisconnectEvent.class, event -> sessionManager.onDisconnect(event));
        NetworkManager networkManager = new NetworkManager(port, sessionManager);

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

        networkManager.start();
    }
}
