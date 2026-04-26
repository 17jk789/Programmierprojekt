package ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing;

import static org.junit.jupiter.api.Assertions.*;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class CommandParserDispatcherTest {
    private class DummyRequest extends Request {
        public DummyRequest(RequestContext context) {
            super(context);
        }
    }

    @Test
    void testSuccessfulParsing() {
        CommandParserDispatcher dispatcher = new CommandParserDispatcher();

        RequestContext ctx = new RequestContext(new SessionId(UUID.randomUUID()), 7411);
        PrimitiveRequest primitiveRequest = new PrimitiveRequest(ctx, "TEST_CMD", List.of());

        dispatcher.register(
                "TEST_CMD",
                new CommandParser<Request>() {
                    @Override
                    public Request parse(PrimitiveRequest primitiveRequest) {
                        return new DummyRequest(primitiveRequest.context());
                    }
                });

        Request result = dispatcher.parse(primitiveRequest);

        assertTrue(result instanceof DummyRequest);
        assertEquals(ctx, result.getContext());
    }

    @Test
    void testUnknownCommandThrows() {
        CommandParserDispatcher dispatcher = new CommandParserDispatcher();

        RequestContext ctx = new RequestContext(new SessionId(UUID.randomUUID()), 42);
        PrimitiveRequest primitiveRequest = new PrimitiveRequest(ctx, "UNKNOWN_CMD", List.of());

        UnknownCommandException ex =
                assertThrows(
                        UnknownCommandException.class, () -> dispatcher.parse(primitiveRequest));

        assertEquals("UNKNOWN_CMD", ex.getMessage());
    }
}
