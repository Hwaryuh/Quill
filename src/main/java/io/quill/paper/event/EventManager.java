package io.quill.paper.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.quill.paper.event.registry.LiveEventPipeline;
import io.quill.paper.event.registry.StaticEventPipeline;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class EventManager {
    private static EventManager instance;
    private JavaPlugin plugin;

    private final List<StaticEventPipeline<?>> pipelines = Lists.newArrayList();
    private final Map<Class<?>, LiveEventPipeline<?, ?>> livePipelines = Maps.newHashMap();

    private EventManager() { }

    public static void initialize(JavaPlugin plugin) {
        checkState(instance == null, "EventManager already initialized");
        EventManager manager = new EventManager();
        manager.plugin = checkNotNull(plugin, "plugin");
        instance = manager;
    }

    public static void shutdown() {
        checkInitialized();
        instance.unregisterAll();
        instance = null;
    }

    private static void checkInitialized() {
        checkState(instance != null, "EventManager not initialized. Call EventManager.initialize() first.");
    }

    public static EventManager getInstance() {
        checkInitialized();
        return instance;
    }

    public <T extends Event> PipelineBuilder<T> register(Class<T> eventType) {
        return new PipelineBuilder<>(this, eventType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event, C extends EventContext> void capture(Class<T> eventType, EventSubscriber<T, C> subscriber) {
        LiveEventPipeline<T, C> registry = (LiveEventPipeline<T, C>) livePipelines.computeIfAbsent(
                eventType,
                k -> new LiveEventPipeline<>(eventType)
        );
        registry.enable(subscriber);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event, C extends EventContext> void release(Class<T> eventType, EventSubscriber<T, C> subscriber) {
        LiveEventPipeline<T, C> registry = (LiveEventPipeline<T, C>) livePipelines.get(eventType);
        if (registry != null) {
            registry.disable(subscriber);
        }
    }

    void addPipeline(StaticEventPipeline<?> pipeline) {
        pipelines.add(pipeline);
    }

    private void unregisterAll() {
        pipelines.forEach(StaticEventPipeline::unregister);
        pipelines.clear();

        livePipelines.values().forEach(LiveEventPipeline::disableAll);
        livePipelines.clear();
    }

    public static final class PipelineBuilder<T extends Event> {
        private final EventManager manager;
        private final Class<T> eventType;
        private final List<EventSubscriber<T, ? extends EventContext>> subscribers = Lists.newArrayList();

        PipelineBuilder(EventManager manager, Class<T> eventType) {
            this.manager = manager;
            this.eventType = eventType;
        }

        public <C extends EventContext> PipelineBuilder<T> subscribe(EventSubscriber<T, C> subscriber) {
            subscribers.add(subscriber);
            return this;
        }

        public void build() {
            StaticEventPipeline<T> pipeline = new StaticEventPipeline<>(eventType);

            for (EventSubscriber<T, ? extends EventContext> subscriber : subscribers) {
                pipeline.subscribe(subscriber);
            }

            pipeline.register(manager.plugin);
            manager.addPipeline(pipeline);
        }
    }
}