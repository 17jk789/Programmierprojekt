package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.User;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.user.UserRegistry;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.Optional;

/** Helper to send responses to sessions identified by usernames. */
public class SessionDispatcher {
    private final ResponseDispatcher responseDispatcher;
    private final UserRegistry userRegistry;

    public SessionDispatcher(ResponseDispatcher responseDispatcher, UserRegistry userRegistry) {
        this.responseDispatcher = responseDispatcher;
        this.userRegistry = userRegistry;
    }

    /**
     * Dispatch a success response built from {@code body} to the user with {@code username}. If the
     * user is disconnected or unknown, the call is a no-op.
     */
    public void dispatchToUser(String username, ResponseBody body) {
        Optional<User> opt = userRegistry.getByUsername(username);
        if (opt.isEmpty()) {
            return;
        }
        User user = opt.get();
        Optional<SessionId> sid = user.getSessionId();
        if (sid.isEmpty()) {
            return;
        }
        RequestContext ctx = new RequestContext(sid.get(), 0);
        SuccessResponse resp = new SuccessResponse(ctx, body) {};
        responseDispatcher.dispatch(resp);
    }

    /** Broadcast the provided {@code body} to every player in the given lobby. */
    public void broadcastToLobby(Lobby lobby, ResponseBody body) {
        for (String username : lobby.getPlayerNames()) {
            dispatchToUser(username, body);
        }
    }
}
