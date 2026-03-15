package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public class UserFactory {
    private final UserRegistry registry;

    public UserFactory(UserRegistry registry) {
        this.registry = registry;
    }

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
