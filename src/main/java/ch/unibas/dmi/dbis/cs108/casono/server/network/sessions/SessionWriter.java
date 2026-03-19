package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import ch.unibas.dmi.dbis.cs108.casono.server.network.response.PrimitiveResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TransportLayer;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionWriter implements Runnable {
    private final TransportLayer transport;
    private final BlockingQueue<PrimitiveResponse> queue;
    private final Logger logger;

    public SessionWriter(Session session) {
        this.transport = session.getTransport();
        this.queue = session.getResponseQueue();
        this.logger =
                LogManager.getLogger(
                        SessionReader.class.toString() + "-" + session.getId().value());
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PrimitiveResponse response = queue.take();
                RawPacket packet = new RawPacket(response.requestId(), response.payload());
                transport.write(packet);
            } catch (IOException e) {
                logger.trace("Unexpected exception while writing to transport", e);
            } catch (InterruptedException e) {
                logger.trace("Thread got interrupted", e);
                break;
            }
        }
    }
}
