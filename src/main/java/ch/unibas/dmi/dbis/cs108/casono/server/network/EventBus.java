package ch.unibas.dmi.dbis.cs108.casono.server.network;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) handler);
    }

    public <T> void publish(T event) {
        List<Consumer<Object>> subscribers = handlers.get(event.getClass());
        if (subscribers != null) {
            subscribers.forEach(h -> h.accept(event));
        }
    }
}
