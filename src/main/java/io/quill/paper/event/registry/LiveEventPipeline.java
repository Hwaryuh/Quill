package io.quill.paper.event.registry;

import io.quill.paper.event.EventContext;
import io.quill.paper.event.EventSubscriber;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class LiveEventPipeline<T extends Event, C extends EventContext> {
    private final JavaPlugin plugin;
    private final Class<T> eventType;
    private final Map<EventSubscriber<T, C>, Listener> subscribers = new IdentityHashMap<>(4);

    public LiveEventPipeline(JavaPlugin plugin, Class<T> eventType) {
        this.plugin = plugin;
        this.eventType = eventType;
    }

    public void enable(EventSubscriber<T, C> subscriber) {
        if (subscribers.containsKey(subscriber)) return;

        Listener listener = new Listener() { };
        EventExecutor executor = EventExecutors.create(eventType, List.of(subscriber));

        plugin.getServer().getPluginManager().registerEvent(eventType, listener, subscriber.getPriority(), executor, plugin, subscriber.ignoreCancelled());
        subscribers.put(subscriber, listener);
    }

    public void disable(EventSubscriber<T, C> subscriber) {
        Listener listener = subscribers.remove(subscriber);
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

    public void disableAll() {
        for (Listener listener : subscribers.values()) {
            HandlerList.unregisterAll(listener);
        }
        subscribers.clear();
    }

    public boolean isEnabled(EventSubscriber<T, C> subscriber) {
        return subscribers.containsKey(subscriber);
    }
}