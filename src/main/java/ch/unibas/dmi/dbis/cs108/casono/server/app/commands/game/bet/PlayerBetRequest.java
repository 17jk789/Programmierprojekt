package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.bet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request for the `BET` command.
 *
 * <p>Contains the optional target `gameId` and the `amount` the player wants to bet.
 */
public class PlayerBetRequest extends Request {
    private final Integer gameId;
    private final int amount;

    /**
     * Creates a new {@code PlayerBetRequest}.
     *
     * @param context the request context
     * @param gameId optional game id of the targeted lobby (may be {@code null})
     * @param amount the bet amount (non-negative)
     */
    public PlayerBetRequest(RequestContext context, Integer gameId, int amount) {
        super(context);
        this.gameId = gameId;
        this.amount = amount;
    }

    /**
     * Returns the optional target game id.
     *
     * @return the game id or {@code null} if not provided
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Returns the requested bet amount.
     *
     * @return the bet amount
     */
    public int getAmount() {
        return amount;
    }
}
