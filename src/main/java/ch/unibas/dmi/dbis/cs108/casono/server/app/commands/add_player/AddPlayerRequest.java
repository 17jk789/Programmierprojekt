package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.add_player;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

/** Request data for the ADD_PLAYER command. */
public class AddPlayerRequest extends Request {
    private final String name;
    private final int chips;

    public AddPlayerRequest(RequestContext context, String name, int chips) {
        super(context);
        this.name = name;
        this.chips = chips;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }
}
