package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/** Parser for GET_HIGHSCORES. */
public class GetHighscoresParser implements CommandParser<GetHighscoresRequest> {
    @Override
    public GetHighscoresRequest parse(PrimitiveRequest primitiveRequest) {
        return new GetHighscoresRequest(primitiveRequest.context());
    }
}
