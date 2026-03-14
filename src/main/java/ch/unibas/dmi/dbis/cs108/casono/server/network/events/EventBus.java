package ch.unibas.dmi.dbis.cs108.casono.server.network.events;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** A simple event bus for publishing and subscribing to events. */
public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();

    /**
     * Subscribes a handler to a specific event type.
     *
     * @param eventType the class of the event to subscribe to
     * @param handler   the consumer to handle the event
     */
    @SuppressWarnings("unchecked") // This cast is safe, because handlers only get passed the type they subscribed to
    public <T extends Event> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) (Consumer<?>) handler);
    }

    /**
     * Publishes an event to all subscribed handlers.
     *
     * @param event the event to publish
     */
    public <T extends Event> void publish(T event) {
        Objects.requireNonNull(event, "event must not be null");
        List<Consumer<Object>> subscribers = handlers.get(event.getClass());
        if (subscribers != null) {
            subscribers.forEach(h -> h.accept(event));
        }
    }
}
