package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

/** Creates new users, resolving name conflicts automatically. */
public class UserFactory {
    private final UserRegistry registry;

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
     * Lars_001, Lars_002, ...).
     *
     * @param desiredName the preferred display name
     * @param sessionId the session to associate with the new user
     * @return the newly created and registered user
     */
    public User create(String desiredName, SessionId sessionId) {
        var result = registry.registerIfAvailable(desiredName, sessionId);
        if (result.isPresent()) {
            return result.get();
        }

        int suffix = 1;
        while (true) {
            String candidate = desiredName + "_" + String.format("%03d", suffix);
            result = registry.registerIfAvailable(candidate, sessionId);
            if (result.isPresent()) {
                return result.get();
            }
            suffix++;
        }
    }
}
