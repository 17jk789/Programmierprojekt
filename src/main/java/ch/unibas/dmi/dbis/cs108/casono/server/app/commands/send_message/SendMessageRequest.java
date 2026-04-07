package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class SendMessageRequest extends Request {

    private final Message msg;

    public SendMessageRequest(RequestContext context, Message msg) {
        super(context);
        this.msg = msg;
    }

    public Message getMessage() {
        return msg;
    }
}
