package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class SendMessageParser implements CommandParser<SendMessageRequest> {
    @Override
    public SendMessageRequest parse(PrimitiveRequest primitiveRequest) {
        Message msg = Message.toMessage(primitiveRequest.parameters());
        return new SendMessageRequest(primitiveRequest.context(), msg);
    }
}
