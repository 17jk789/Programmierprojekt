package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.list_users;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class ListUsersParser implements CommandParser<ListUsersRequest> {
    @Override
    public ListUsersRequest parse(PrimitiveRequest primitiveRequest) {
        return new ListUsersRequest(primitiveRequest.context());
    }
}
