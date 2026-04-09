package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;

public class SendMessageParser implements CommandParser<SendMessageRequest> {

    /**
     * Parses a raw {@link PrimitiveRequest} into a specific {@link SendMessageRequest}.
     * This method extracts the message details from the request parameters and
     * wraps them along with the request context into a structured request object.
     *
     * @param primitiveRequest The raw request containing parameters and context from the network.
     * @return A structured {@link SendMessageRequest} containing the parsed {@link Message}.
     */
    @Override
    public SendMessageRequest parse(PrimitiveRequest primitiveRequest) {
        Message msg = Message.toMessageReqPars(primitiveRequest.parameters());
        return new SendMessageRequest(primitiveRequest.context(), msg);
    }
}
