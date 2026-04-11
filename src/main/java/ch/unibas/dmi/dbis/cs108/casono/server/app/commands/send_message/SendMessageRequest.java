package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.send_message;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class SendMessageRequest extends Request {

    private final Message msg;

    /**
     * Constructs a new SendMessageRequest with the given context and message.
     *
     * @param context The {@link RequestContext} associated with this request.
     * @param msg The {@link Message} object to be processed.
     */
    public SendMessageRequest(RequestContext context, Message msg) {
        super(context);
        this.msg = msg;
    }

    /**
     * Returns the message contained within this request.
     *
     * @return The {@link Message} instance.
     */
    public Message getMessage() {
        return msg;
    }
}
