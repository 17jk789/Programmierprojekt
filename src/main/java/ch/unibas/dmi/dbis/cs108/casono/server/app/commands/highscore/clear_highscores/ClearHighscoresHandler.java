package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.highscore.HighscoreService;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.OkResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/** Handler for CLEAR_HIGHSCORES. */
public class ClearHighscoresHandler extends CommandHandler<ClearHighscoresRequest> {
    private final HighscoreService highscoreService;

    public ClearHighscoresHandler(ResponseDispatcher responseDispatcher) {
        super(responseDispatcher);
        this.highscoreService = HighscoreService.getInstance();
    }

    @Override
    public void execute(ClearHighscoresRequest request) {
        highscoreService.clearAll();
        responseDispatcher.dispatch(new OkResponse(request.getContext()));
    }
}
