package ch.unibas.dmi.dbis.cs108.casono.server.network.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventBusTest {
    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
    }

    static class TestEvent implements Event {}

    @Test
    void singleSubscriberReceivesEvent() {
        AtomicBoolean called = new AtomicBoolean(false);
        eventBus.subscribe(TestEvent.class, e -> called.set(true));
        eventBus.publish(new TestEvent());
        assertTrue(called.get());
    }

    @Test
    void multipleSubscribersReceiveEvent() {
        AtomicInteger counter = new AtomicInteger(0);
        eventBus.subscribe(TestEvent.class, e -> counter.incrementAndGet());
        eventBus.subscribe(TestEvent.class, e -> counter.incrementAndGet());
        eventBus.publish(new TestEvent());
        assertEquals(2, counter.get());
    }

    @Test
    void publishNullThrowsNpe() {
        assertThrows(NullPointerException.class, () -> eventBus.publish((Event) null));
    }

    @Test
    void subscribingToBaseEventDoesNotReceiveSubclass() {
        AtomicBoolean called = new AtomicBoolean(false);
        eventBus.subscribe(Event.class, e -> called.set(true));
        eventBus.publish(new TestEvent());
        assertFalse(called.get());
    }
}
