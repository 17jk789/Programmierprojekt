package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

/**
 * Parser for the `BET` command.
 *
 * <p>Parses the request parameters and builds a {@link PlayerBetRequest}. Expected parameters:
 *
 * <ul>
 *   <li>`GAME_ID` (optional) - numeric id of the lobby/game to target
 *   <li>`AMOUNT` (required) - amount to bet
 * </ul>
 */
public class PlayerBetParser implements CommandParser<PlayerBetRequest> {
    /**
     * Parse a primitive request into a {@link PlayerBetRequest}.
     *
     * @param primitiveRequest the raw parsed request containing parameters and context
     * @return a {@link PlayerBetRequest} with the parsed values (gameId may be null)
     */
    @Override
    public PlayerBetRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor =
                new RequestParameterAccessor(primitiveRequest.parameters());

        Integer gameId = null;
        try {
            gameId = accessor.optional("GAME_ID", null, Integer::parseInt);
        } catch (Exception e) {
            // parse handled by framework
        }

        int amount = accessor.require("AMOUNT", Integer::parseInt);

        return new PlayerBetRequest(primitiveRequest.context(), gameId, amount);
    }
}
