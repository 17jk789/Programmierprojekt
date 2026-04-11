package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.create_lobby;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyId;
import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class CreateLobbyHandler extends CommandHandler<CreateLobbyRequest> {
    private final LobbyManager lobbyManager;

    public CreateLobbyHandler(ResponseDispatcher responseDispatcher, LobbyManager lobbyManager) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
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
    }
}
