package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Creates new users, resolving name conflicts automatically. */
public class UserFactory {
    private final UserRegistry registry;
    private final AtomicInteger anonymousCounter = new AtomicInteger(1);
    private static final Logger LOGGER = LogManager.getLogger(UserFactory.class.getSimpleName());

    /**
     * Creates a new UserFactory backed by the given registry.
     *
     * @param registry the registry to register new users to
     */
    public UserFactory(UserRegistry registry) {
        this.registry = registry;
    }

    /**
     * Creates and registers a new user with the given name and session. If the name is already
     * taken, a numeric suffix is appended and incremented until a free name is found (e.g.
     * Lars_001, Lars_002, ...). If the desiredName is null or empty, an automatic name of the form
     * "playerN" is assigned, incrementing N until a free name is found.
     *
     * @param desiredName the preferred display name
     * @param sessionId the session to associate with the new userwas
     * @return the newly created and registered user
     */
    public User create(String desiredName, SessionId sessionId) {
        if (desiredName == null || desiredName.isBlank()) {
            // assign playerN names for anonymous logins
            while (true) {
                String candidate = "player" + anonymousCounter.getAndIncrement();
                var result = registry.registerIfAvailable(candidate, sessionId);
                if (result.isPresent()) {
                    var user = result.get();
                    LOGGER.info(
                            "Registered user '{}' with id {} for session {}",
                            user.getName(),
                            user.getId().value(),
                            sessionId == null ? "null" : sessionId.value());
                    return user;
                }
            }
        }

        var result = registry.registerIfAvailable(desiredName, sessionId);
        if (result.isPresent()) {
            var user = result.get();
            LOGGER.info(
                    "Registered user '{}' with id {} for session {}",
                    user.getName(),
                    user.getId().value(),
                    sessionId == null ? "null" : sessionId.value());
            return user;
        }

        int suffix = 1;
        while (true) {
            String candidate = desiredName + "_" + String.format("%03d", suffix);
            result = registry.registerIfAvailable(candidate, sessionId);
            if (result.isPresent()) {
                var user = result.get();
                LOGGER.info(
                        "Registered user '{}' with id {} for session {}",
                        user.getName(),
                        user.getId().value(),
                        sessionId == null ? "null" : sessionId.value());
                return user;
            }
            suffix++;
        }
    }
}
