package io.quill.paper.event;

public enum EventResult {
    /**
     * 이벤트를 처리했으며, 이벤트를 취소하고 다음 구독자에게 전달하지 않습니다.
     */
    TERMINATE,

    /**
     * 이벤트를 처리했지만 취소하지 않습니다.
     */
    STOP,

    /**
     * 이벤트를 처리하지 않았으며, 다음 구독자에게 전달합니다.
     */
    PASS
}