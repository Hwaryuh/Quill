package io.quill.paper.util.bukkit.task;

/**
 * Bukkit 스케줄러와 연결된 태스크의 내부 인터페이스.
 */
interface BukkitBackedTask extends CancellableTask {
    int getTaskId();

    boolean isRepeating();
}