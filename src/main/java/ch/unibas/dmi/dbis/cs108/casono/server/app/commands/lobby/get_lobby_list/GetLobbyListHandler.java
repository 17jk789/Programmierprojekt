package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.lobby.get_lobby_list;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.LobbyManager;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;
import java.util.Collection;

/**
 * Handler for the `GET_LOBBY_LIST` command.
 *
 * <p>Queries the {@link LobbyManager} for all available lobbies and dispatches a {@link
 * GetLobbyListResponse} containing basic metadata for each lobby (`ID`, `NAME`, `PLAYER_COUNT`).
 */
public class GetLobbyListHandler extends CommandHandler<GetLobbyListRequest> {
    private final LobbyManager lobbyManager;

    public GetLobbyListHandler(ResponseDispatcher responseDispatcher, LobbyManager lobbyManager) {
        super(responseDispatcher);
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void execute(GetLobbyListRequest request) {
        Collection<ch.unibas.dmi.dbis.cs108.casono.server.domain.lobby.Lobby> l =
                lobbyManager.getAllLobbies();
        responseDispatcher.dispatch(new GetLobbyListResponse(request.getContext(), l));
    }
}
