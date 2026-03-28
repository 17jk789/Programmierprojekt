package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionDisconnectJob implements Runnable {
    private final Logger logger;
    private final SessionManager sessionManager;
    private final EventBus eventBus;
    private final Duration timeoutThreshold;

    public SessionDisconnectJob(
            SessionManager sessionManager, EventBus eventBus, Duration timeoutThreshold) {
        this.logger = LogManager.getLogger(SessionDisconnectJob.class);
        this.sessionManager = sessionManager;
        this.eventBus = eventBus;
        this.timeoutThreshold = timeoutThreshold;
    }

    @Override
    public void run() {
        logger.debug("Job started.");
        Instant threshold = Instant.now().minus(timeoutThreshold);

        for (Session session : sessionManager.getAllSessions()) {
            if (session.getLastInboundActivity().isBefore(threshold)) {
                eventBus.publish(new DisconnectEvent(session.getId()));
                logger.info(
                        "Initiated disconnect of {}, as it hasn't been active since a while",
                        session.getId());
            }
        }

        logger.debug("Job finished.");
    }
}
