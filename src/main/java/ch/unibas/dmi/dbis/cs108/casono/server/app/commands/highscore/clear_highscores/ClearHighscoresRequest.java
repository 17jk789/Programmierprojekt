package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.clear_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request object for CLEAR_HIGHSCORES. */
public class ClearHighscoresRequest extends Request {
    public ClearHighscoresRequest(RequestContext context) {
        super(context);
    }
}
