package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.Session;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class CreateLobbyHandler extends CommandHandler<CreateLobbyRequest> {
    private final LobbyManager lobbyManager;
    private final SessionManager sessionManager;

    public CreateLobbyHandler(
            ResponseDispatcher responseDispatcher, LobbyManager lobbyManager, SessionManager sessionManager) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void execute(CreateLobbyRequest request) {
        LobbyId id = lobbyManager.createNewLobby(null);

        if (id == null) {
            responseDispatcher.dispatch(
                    new ErrorResponse(
                            request.getContext(),
                            "LOBBIES_FULL",
                            "Maximum number of 8 lobbies reached"));
            return;
        }

        responseDispatcher.dispatch(new CreateLobbyResponse(request.getContext(), id.value()));

        // broadcast LOBBY_CREATED event to all connected sessions (requestId=0)
        for (Session s : sessionManager.getAllSessions()) {
            RequestContext ctx = new RequestContext(s.getId(), 0);
            SuccessResponse ev =
                    new SuccessResponse(
                            ctx,
                            ResponseBody.builder()
                                    .param("EVENT", "LOBBY_CREATED")
                                    .param("LOBBY_ID", id.value())
                                    .build()) {};

            responseDispatcher.dispatch(ev);
        }
    }
}
