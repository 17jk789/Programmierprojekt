package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.game.raise;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/**
 * Request for the `RAISE` command.
 *
 * <p>
 * Contains the optional target `gameId` and the `amount` the player wants to
 * raise.
 */
public class PlayerRaiseRequest extends Request {
    private final Integer gameId;
    private final int amount;

    /**
     * Creates a new {@code PlayerRaiseRequest}.
     *
     * @param context the request context
     * @param gameId  optional game id of the targeted lobby (may be {@code null})
     * @param amount  the raise amount (non-negative)
     */
    public PlayerRaiseRequest(RequestContext context, Integer gameId, int amount) {
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
     * Returns the requested raise amount.
     *
     * @return the raise amount
     */
    public int getAmount() {
        return amount;
    }
}
