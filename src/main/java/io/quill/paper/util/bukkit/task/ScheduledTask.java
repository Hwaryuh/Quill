package io.quill.paper.util.bukkit.task;

public interface ScheduledTask {
    /**
     * Task 취소 시도
     * @return 취소 성공 여부. 이미 취소된 경우 false 반환
     */
    boolean cancel();

    /**
     * Task가 취소되었는지 여부
     * @return 취소된 경우 true
     */
    boolean isCancelled();

    /**
     * Bukkit task ID
     * @return task ID
     */
    int getTaskId();

    /**
     * 반복 태스크 여부
     * @return 반복 태스크인 경우 true
     */
    boolean isRepeating();
}