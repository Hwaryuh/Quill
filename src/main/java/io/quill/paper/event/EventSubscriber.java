package io.quill.paper.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.Optional;

/**
 * 이벤트 구독자 인터페이스.
 * 특정 이벤트 타입을 구독하고 처리합니다.
 *
 * @param <T> 구독할 이벤트 타입
 * @param <C> 이벤트 컨텍스트 타입
 */
public interface EventSubscriber<T extends Event, C extends EventContext> {
    /**
     * 이벤트 처리 여부와 컨텍스트를 결정합니다.
     *
     * @param event 확인할 이벤트
     * @return 처리할 경우 컨텍스트를 담은 Optional, 무시할 경우 empty
     */
    Optional<C> expect(T event);

    /**
     * 이벤트를 처리합니다.
     * Error context는 자동으로 onError()로 라우팅됩니다.
     *
     * @param event 처리할 이벤트
     * @param context expect()에서 반환한 컨텍스트
     * @return 이벤트 처리 결과
     */
    default EventResult handle(T event, C context) {
        if (context instanceof EventContext.Error error) {
            return onError(event, error);
        }
        return onEvent(event, context);
    }

    /**
     * 일반 컨텍스트를 처리합니다.
     * 구현 필요.
     *
     * @param event 처리할 이벤트
     * @param context 컨텍스트 (Error가 아닌 경우)
     * @return 이벤트 처리 결과
     */
    EventResult onEvent(T event, C context);

    /**
     * 에러 컨텍스트를 처리합니다.
     * 오버라이드 가능.
     *
     * @param event 처리할 이벤트
     * @param error 에러 컨텍스트
     * @return 이벤트 처리 결과
     */
    default EventResult onError(T event, EventContext.Error error) {
        return EventResult.TERMINATE;
    }

    /**
     * 이 구독자의 실행 우선순위를 반환합니다.
     * {@link Listen} 어노테이션보다 우선순위가 높습니다.
     * 명시적 Override는 설정을 덮어씁니다.
     *
     * @return Bukkit 이벤트 우선순위
     * @see Listen#priority()
     */
    default EventPriority getPriority() {
        Listen config = this.getClass().getAnnotation(Listen.class);
        return config != null ? config.priority() : EventPriority.NORMAL;
    }

    /**
     * 취소된 이벤트를 무시할지 여부를 반환합니다.
     * {@link Listen} 어노테이션보다 우선순위가 높습니다.
     * 명시적 Override는 설정을 덮어씁니다.
     *
     * @return 취소된 이벤트 무시 여부
     * @see Listen#ignoreCancelled()
     */
    default boolean ignoreCancelled() {
        Listen config = this.getClass().getAnnotation(Listen.class);
        return config != null && config.ignoreCancelled();
    }
}