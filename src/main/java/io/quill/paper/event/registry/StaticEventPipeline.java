package io.quill.paper.event.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.quill.paper.event.EventContext;
import io.quill.paper.event.EventSubscriber;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 이벤트 구독자를 등록하고 관리하는 레지스트리.
 * 여러 구독자를 우선순위별로 그룹화하여 실행합니다.
 *
 * @param <T> 처리할 이벤트 타입
 */
public class StaticEventPipeline<T extends Event> {
    private final Class<T> eventType;
    private final List<EventSubscriber<T, ? extends EventContext>> subscribers = Lists.newArrayList();
    private final Map<HandlerContext, Listener> listeners = Maps.newHashMap();

    /**
     * 새 이벤트 구독자 레지스트리를 생성합니다.
     *
     * @param eventType 처리할 이벤트 타입
     */
    public StaticEventPipeline(Class<T> eventType) {
        this.eventType = eventType;
    }

    /**
     * 구독자를 등록합니다.
     * 같은 우선순위 내에서는 등록 순서대로 실행됩니다.
     *
     * @param subscriber 등록할 구독자
     * @return this (체이닝용)
     */
    public <C extends EventContext> StaticEventPipeline<T> subscribe(EventSubscriber<T, C> subscriber) {
        subscribers.add(subscriber);
        return this;
    }

    /**
     * 등록된 모든 구독자를 Bukkit에 등록합니다.
     *
     * @param plugin 플러그인 인스턴스
     */
    public void register(JavaPlugin plugin) {
        if (subscribers.isEmpty()) return;

        Multimap<HandlerContext, EventSubscriber<T, ? extends EventContext>> groupedSubscribers = ArrayListMultimap.create();

        for (EventSubscriber<T, ? extends EventContext> subscriber : subscribers) {
            HandlerContext key = new HandlerContext(subscriber.getPriority(), subscriber.ignoreCancelled());
            groupedSubscribers.put(key, subscriber);
        }

        for (Map.Entry<HandlerContext, Collection<EventSubscriber<T, ? extends EventContext>>> entry : groupedSubscribers.asMap().entrySet()) {
            HandlerContext key = entry.getKey();
            List<EventSubscriber<T, ? extends EventContext>> groupSubscribers = Lists.newArrayList(entry.getValue());
            EventExecutor executor = createExecutor(groupSubscribers);

            Listener listener = new Listener() { };
            listeners.put(key, listener);

            plugin.getServer().getPluginManager().registerEvent(eventType, listener, key.priority(), executor, plugin, key.ignoreCancelled());
        }
    }

    @SuppressWarnings("unchecked")
    private EventExecutor createExecutor(List<EventSubscriber<T, ? extends EventContext>> prioritySubscribers) {
        return EventExecutors.create(eventType, (List) prioritySubscribers);
    }

    /**
     * 등록된 모든 구독자를 해제합니다.
     */
    public void unregister() {
        for (Listener listener : listeners.values()) {
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
    }

    private record HandlerContext(EventPriority priority, boolean ignoreCancelled) { }
}