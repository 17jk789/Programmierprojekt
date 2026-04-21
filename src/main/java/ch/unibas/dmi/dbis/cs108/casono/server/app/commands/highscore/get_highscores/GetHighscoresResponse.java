package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.highscore.get_highscores;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import java.util.List;

/** Success response containing repeated HIGHSCORE parameters. */
public class GetHighscoresResponse extends SuccessResponse {
    public GetHighscoresResponse(RequestContext context, List<String> entries) {
        super(
                context,
                ResponseBody.builder()
                        .block(
                                "HIGHSCORES",
                                block -> {
                                    for (String entry : entries) {
                                        block.param("HIGHSCORE", entry);
                                    }
                                })
                        .build());
    }
}
