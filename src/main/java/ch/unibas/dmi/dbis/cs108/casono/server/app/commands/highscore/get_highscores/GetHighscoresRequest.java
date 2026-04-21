package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request object for GET_HIGHSCORES. */
public class GetHighscoresRequest extends Request {
    public GetHighscoresRequest(RequestContext context) {
        super(context);
    }
}
