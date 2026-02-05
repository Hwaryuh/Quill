package io.quill.paper.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * 이벤트 처리 컨텍스트의 베이스 타입.
 */
public interface EventContext {

    /**
     * 컨텍스트가 필요 없는 경우
     */
    record Empty() implements EventContext { }

    /**
     * 에러 상태를 나타내는 컨텍스트
     */
    interface Error extends EventContext {
        default String plainText() {
            return PlainTextComponentSerializer.plainText().serialize(text());
        }

        Component text();
    }

    /**
     * 데이터를 전달하는 컨텍스트
     */
    interface Data extends EventContext { }
}