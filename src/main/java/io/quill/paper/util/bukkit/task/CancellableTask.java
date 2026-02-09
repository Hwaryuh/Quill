package io.quill.paper.util.bukkit.task;

/**
 * 취소 가능한 스케줄된 태스크.
 */
public interface CancellableTask {
    /**
     * 태스크 취소를 시도한다.
     * @return 이 호출로 인해 태스크가 취소되었으면 true, 이미 취소되었거나 완료된 경우 false
     */
    boolean cancel();

    /**
     * 태스크가 취소되었는지 확인한다.
     * @return 취소된 경우 true
     */
    boolean isCancelled();
}