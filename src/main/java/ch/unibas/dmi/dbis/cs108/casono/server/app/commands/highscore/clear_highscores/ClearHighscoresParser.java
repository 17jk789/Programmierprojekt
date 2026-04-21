package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/** Parser for CLEAR_HIGHSCORES. */
public class ClearHighscoresParser implements CommandParser<ClearHighscoresRequest> {
    @Override
    public ClearHighscoresRequest parse(PrimitiveRequest primitiveRequest) {
        return new ClearHighscoresRequest(primitiveRequest.context());
    }
}
