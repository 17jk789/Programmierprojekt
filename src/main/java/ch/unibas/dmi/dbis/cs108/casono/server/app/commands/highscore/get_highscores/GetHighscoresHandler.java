package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.domain.highscore.HighscoreService;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

/** Handler for GET_HIGHSCORES. */
public class GetHighscoresHandler extends CommandHandler<GetHighscoresRequest> {
    private final HighscoreService highscoreService;

    public GetHighscoresHandler(ResponseDispatcher responseDispatcher) {
        super(responseDispatcher);
        this.highscoreService = HighscoreService.getInstance();
    }

    @Override
    public void execute(GetHighscoresRequest request) {
        responseDispatcher.dispatch(
                new GetHighscoresResponse(
                        request.getContext(), highscoreService.readFormattedEntries()));
    }
}
