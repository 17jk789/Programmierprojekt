package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

/** Parses a primitive request into a {@link ListUsersRequest}. */
public class ListUsersParser implements CommandParser<ListUsersRequest> {
    /**
     * Parses a primitive request into a ListUsersRequest.
     *
     * @param primitiveRequest the request to parse
     * @return the created {@link ListUsersRequest}
     */
    @Override
    public ListUsersRequest parse(PrimitiveRequest primitiveRequest) {
        return new ListUsersRequest(primitiveRequest.context());
    }
}
