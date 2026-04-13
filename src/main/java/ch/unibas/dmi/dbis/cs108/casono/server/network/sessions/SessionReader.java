package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandRouter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParserDispatcher;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.UnknownCommandException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.ProtocolParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.ProtocolParserException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer.TokenizerException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RawRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.MissingParameterException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.ErrorResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.PrimitiveResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatchException;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseEncoder;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
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
            RawRequest rawRequest = null;
            RequestContext requestContext = null;
            try {
                rawPacket = transport.read();
                session.updateLastInboundActivity();
                logger.debug("Recieved: {}", rawPacket);
                processRawPacket(rawPacket);
            } catch (EOFException e) {
                logger.info("Client disconnected");
                eventBus.publish(new DisconnectEvent(session.getId()));
                break;

            } catch (SocketException e) {
                // Connection reset or other socket-level error — treat as client disconnect
                logger.info("Client socket error / connection reset: {}", e.getMessage());
                eventBus.publish(new DisconnectEvent(session.getId()));
                break;

            } catch (TokenizerException | ProtocolParserException e) {
                logger.error("Error occured while parsing request. RawPacket: {}", rawPacket, e);

                sendErrorResponse(
                        new ErrorResponse(
                                requestContext,
                                "PARSING_ERROR",
                                "Error occured during parsing. Likely due to malformed payload."));

            } catch (UnknownCommandException e) {
                logger.error("Recieved unknown command '{}' from client", rawRequest.command(), e);
                sendErrorResponse(
                        new ErrorResponse(
                                requestContext,
                                "UNKNOWN_COMMAND",
                                "This command is unknown to the server."));

            } catch (ResponseDispatchException e) {
                logger.error(
                        "Unexpected ResponseDispatchException exception while dispatching request",
                        e);

            } catch (MissingParameterException e) {
                logger.error(
                        "Recieved request for command '{}' was missing the '{}' parameter",
                        rawRequest.command(),
                        e.getParameterKey());
                sendErrorResponse(
                        new ErrorResponse(requestContext, "MISSING_PARAMETER", e.getMessage()));

            } catch (IOException e) {
                logger.error("Unexpected IO exception while reading from transport", e);

            } catch (RuntimeException e) {
                logger.error("Unexpected RuntimeException occured", e);
                sendErrorResponse(
                        new ErrorResponse(
                                requestContext,
                                "INTERNAL_ERROR",
                                "Unexpected internal server error occured."));
            }
        }
    }

    private void processRawPacket(RawPacket rawPacket)
            throws TokenizerException,
                    ProtocolParserException,
                    UnknownCommandException,
                    ResponseDispatchException,
                    MissingParameterException,
                    IOException {
        RawRequest rawRequest = ProtocolParser.parse(rawPacket.payload());
        logger.debug("Parsed request to {}", rawRequest);

        RequestContext requestContext = new RequestContext(session.getId(), rawPacket.requestId());

        PrimitiveRequest primitiveRequest =
                new PrimitiveRequest(requestContext, rawRequest.command(), rawRequest.parameters());
        logger.debug("Converted to {}", primitiveRequest);

        Request request = dispatcher.parse(primitiveRequest);
        router.execute(request);
    }

    /**
     * Helperfunction to send ErrorResponse to client
     *
     * @param response to send to the client
     */
    private void sendErrorResponse(ErrorResponse response) {
        PrimitiveResponse primitiveResponse = ResponseEncoder.encode(response);
        try {
            session.getResponseQueue().put(primitiveResponse);
        } catch (InterruptedException e) {
            logger.error("Got interrupted while sending ErrorResponse to client.");
        }
    }
}
