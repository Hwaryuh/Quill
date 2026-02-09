package io.quill.paper.util.bukkit.task;

/**
 * 태스크 체인의 개별 스텝을 나타내는 sealed interface.
 */
sealed interface TaskStep {
    record Sync(Runnable action) implements TaskStep { }

    record Async(Runnable action) implements TaskStep { }

    record Delay(long ticks) implements TaskStep { }
}