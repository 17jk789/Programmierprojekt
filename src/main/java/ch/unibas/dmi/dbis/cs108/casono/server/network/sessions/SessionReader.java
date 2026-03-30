package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.CommandRouter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.ProtocolParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.ProtocolParserException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.request.RawRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.TokenizerException;
import java.io.EOFException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionReader implements Runnable {
    private final Session session;
    private final TransportLayer transport;
    private final EventBus eventBus;
    private final CommandParserDispatcher dispatcher;
    private final CommandRouter router;
    private final Logger logger;

    public SessionReader(Session session, EventBus eventBus) {
        this.session = session;
        this.transport = session.getTransport();
        this.eventBus = eventBus;
        this.dispatcher = session.getDispatcher();
        this.router = session.getRouter();
        this.logger =
                LogManager.getLogger(
                        SessionReader.class.toString() + "-" + session.getId().value());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            RawPacket rawPacket = null;
            try {
                rawPacket = transport.read();
                session.updateLastInboundActivity();
                logger.debug("Recieved: {}", rawPacket);

                RawRequest rawRequest = ProtocolParser.parse(rawPacket.payload());
                logger.debug("Parsed request to {}", rawRequest);

                RequestContext requestContext =
                        new RequestContext(session.getId(), rawPacket.requestId());
                PrimitiveRequest primitiveRequest =
                        new PrimitiveRequest(
                                requestContext, rawRequest.command(), rawRequest.parameters());
                logger.debug("Converted to {}", primitiveRequest);

                Request request = dispatcher.parse(primitiveRequest);

                router.execute(request);
            } catch (EOFException e) {
                logger.info("Client disconnected");
                eventBus.publish(new DisconnectEvent(session.getId()));
                break;
            } catch (TokenizerException | ProtocolParserException e) {
                logger.error("Error occured while parsing request. RawPacket: {}", rawPacket, e);

                // TODO: Send error response to client
            } catch (IOException e) {
                logger.error("Unexpected exception while reading from transport", e);
            }
        }
    }
}
