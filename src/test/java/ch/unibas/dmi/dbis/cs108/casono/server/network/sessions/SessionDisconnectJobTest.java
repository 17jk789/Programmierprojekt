package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ch.unibas.dmi.dbis.cs108.casono.server.network.events.DisconnectEvent;
import ch.unibas.dmi.dbis.cs108.casono.server.network.events.EventBus;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SessionDisconnectJobTest {
    private EventBus eventBus;
    private SessionManager sessionManager;

    @BeforeEach
    void initEnvironment() {
        eventBus = new EventBus();
        sessionManager = new SessionManager(eventBus, null);
    }

    @Test
    void testDisconnectValidSession() {
        SessionDisconnectJob disconnectJob =
                new SessionDisconnectJob(sessionManager, eventBus, Duration.ofSeconds(30));

        sessionManager.create(null, null);
        sessionManager.create(null, null);

        AtomicBoolean disconnectOccurred = new AtomicBoolean(false);
        eventBus.subscribe(DisconnectEvent.class, (event) -> disconnectOccurred.set(true));

        disconnectJob.run();
        assertFalse(disconnectOccurred.get());
    }

    @Test
    void testDisconnectExpiredSession()
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        SessionDisconnectJob disconnectJob =
                new SessionDisconnectJob(sessionManager, eventBus, Duration.ofSeconds(30));

        sessionManager.create(null, null);
        Session expiredSession = sessionManager.create(null, null);
        Field lastActivityField = Session.class.getDeclaredField("lastActivity");
        lastActivityField.setAccessible(true);
        lastActivityField.set(expiredSession, Instant.now().minus(35, ChronoUnit.SECONDS));

        AtomicReference<DisconnectEvent> disconnectEvent = new AtomicReference<>();
        eventBus.subscribe(DisconnectEvent.class, (event) -> disconnectEvent.set(event));

        disconnectJob.run();
        assertEquals(disconnectEvent.get().sessionId(), expiredSession.getId());
    }
}
