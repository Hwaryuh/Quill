package io.quill.paper.util.bukkit.task;

import org.bukkit.scheduler.BukkitTask;

import static com.google.common.base.Preconditions.checkNotNull;

final class QuillScheduledTask implements ScheduledTask {
    private final BukkitTask task;
    private final boolean repeating;

    QuillScheduledTask(BukkitTask task, boolean repeating) {
        this.task = checkNotNull(task, "task");
        this.repeating = repeating;
    }

    @Override
    public boolean cancel() {
        if (task.isCancelled()) {
            return false;
        }
        task.cancel();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public int getTaskId() {
        return task.getTaskId();
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }
}