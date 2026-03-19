package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.ProtocolParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.parser.ProtocolParserException;
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
    private final Logger logger;

    public SessionReader(Session session, EventBus eventBus) {
        this.session = session;
        this.transport = session.getTransport();
        this.eventBus = eventBus;
        this.logger =
                LogManager.getLogger(
                        SessionReader.class.toString() + "-" + session.getId().value());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                RawPacket rawPacket = transport.read();
                logger.debug("Recieved: {}", rawPacket);

                PrimitiveRequest primitiveRequest = ProtocolParser.parse(rawPacket);
                logger.debug("Parsed request to {}", primitiveRequest);
            } catch (EOFException e) {
                logger.info("Client disconnected");
                eventBus.publish(new DisconnectEvent(session.getId()));
                break;
            } catch (TokenizerException | ProtocolParserException e) {
                logger.trace("Error occured while parsing request", e);

                // TODO: Send error response to client
            } catch (IOException e) {
                logger.trace("Unexpected exception while reading from transport", e);
            }
        }
    }
}
