package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Periodically periodicly run job to remove disconnected users who have exceeded the reconnect
 * threshold.
 */
public class UserCleanupJob implements Runnable {
    private static final Logger logger = LogManager.getLogger(UserCleanupJob.class);

    private final UserRegistry registry;
    private final Duration reconnectThreshold;

    public UserCleanupJob(UserRegistry registry, Duration reconnectThreshold) {
        this.registry = registry;
        this.reconnectThreshold = reconnectThreshold;
    }

    @Override
    public void run() {
        logger.debug("Job started.");
        Instant threshold = Instant.now().minus(reconnectThreshold);

        for (User user : registry.getAllUsers()) {
            if (user.getDisconnectedAt().isEmpty()) {
                continue;
            }

            Instant disconnectedAt = user.getDisconnectedAt().get();
            if (disconnectedAt.isBefore(threshold)) {
                if (registry.removeIfStillDisconnected(user.getId())) {
                    logger.info("Removed expired user {} ({})", user.getName(), user.getId().value());
                }
            }
        }

        logger.debug("Job finished.");
    }
}
