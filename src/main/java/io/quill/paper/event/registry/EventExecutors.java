package io.quill.paper.event.registry;

import io.quill.paper.event.EventContext;
import io.quill.paper.event.EventResult;
import io.quill.paper.event.EventSubscriber;
import io.quill.paper.util.bukkit.Logger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

import java.util.List;
import java.util.Optional;

final class EventExecutors {
    private EventExecutors() { }

    static <T extends Event> EventExecutor create(Class<T> eventType, List<? extends EventSubscriber<T, ? extends EventContext>> subscribers) {
        return (listener, event) -> {
            if (!eventType.isInstance(event)) return;

            @SuppressWarnings("unchecked")
            T typedEvent = (T) event;

            for (EventSubscriber<T, ? extends EventContext> subscriber : subscribers) {
                try {
                    boolean isPass = executeSubscriber(subscriber, typedEvent);
                    if (!isPass) break;
                } catch (Exception e) {
                    Logger.error("Error in event subscriber: " + subscriber.getClass().getName(), e);
                }
            }
        };
    }

    private static <T extends Event, C extends EventContext> boolean executeSubscriber(EventSubscriber<T, C> subscriber, T event) {
        Optional<C> contextOpt = subscriber.expect(event);
        if (contextOpt.isEmpty()) return true;

        C context = contextOpt.get();
        EventResult result = subscriber.handle(event, context);

        return switch (result) {
            case TERMINATE -> {
                if (event instanceof Cancellable c) c.setCancelled(true);
                yield false;
            }
            case STOP -> false;
            case PASS -> true;
        };
    }
}