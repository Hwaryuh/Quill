package io.quill.paper.util.bukkit.task;

import io.quill.paper.Quill;
import io.quill.paper.util.bukkit.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Tasks {
    private static final JavaPlugin PLUGIN = Quill.getInstance().getPlugin();

    private Tasks() { }

    public static void run(Runnable task) {
        checkNotNull(task, "task");
        PLUGIN.getServer().getScheduler().runTask(PLUGIN, wrapTask(task));
    }

    public static ScheduledTask later(long delay, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");

        return wrap(PLUGIN.getServer().getScheduler().runTaskLater(PLUGIN, wrapTask(task), delay), false);
    }

    public static ScheduledTask repeat(long delay, long period, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        return wrap(PLUGIN.getServer().getScheduler().runTaskTimer(PLUGIN, wrapTask(task), delay, period), true);
    }

    // async

    public static void runAsync(Runnable task) {
        checkNotNull(task, "task");
        PLUGIN.getServer().getScheduler().runTaskAsynchronously(PLUGIN, wrapTask(task));
    }

    public static ScheduledTask laterAsync(long delay, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");

        return wrap(PLUGIN.getServer().getScheduler().runTaskLaterAsynchronously(PLUGIN, wrapTask(task), delay), false);
    }

    public static ScheduledTask repeatAsync(long delay, long period, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        return wrap(PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(PLUGIN, wrapTask(task), delay, period), true);
    }

    // cancelable

    public static ScheduledTask repeat(long delay, long period, Consumer<ScheduledTask> task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        CancellableTaskWrapper wrapper = new CancellableTaskWrapper();
        BukkitTask bukkitTask = PLUGIN.getServer().getScheduler().runTaskTimer(
                PLUGIN,
                wrapTask(() -> task.accept(wrapper.get())),
                delay,
                period
        );

        wrapper.set(wrap(bukkitTask, true));
        return wrapper.get();
    }

    public static ScheduledTask repeatAsync(long delay, long period, Consumer<ScheduledTask> task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 1, "delay must be at least 1 tick for async tasks");
        checkArgument(period > 0, "period must be positive");

        CancellableTaskWrapper wrapper = new CancellableTaskWrapper();
        BukkitTask bukkitTask = PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(
                PLUGIN,
                wrapTask(() -> task.accept(wrapper.get())),
                delay,
                period
        );

        wrapper.set(wrap(bukkitTask, true));
        return wrapper.get();
    }

    private static Runnable wrapTask(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Throwable t) {
                Logger.error("Exception in scheduled task", t);
                throw t;
            }
        };
    }

    private static ScheduledTask wrap(BukkitTask task, boolean repeating) {
        return new QuillScheduledTask(task, repeating);
    }

    private static final class CancellableTaskWrapper {
        private final AtomicReference<ScheduledTask> task = new AtomicReference<>();

        void set(ScheduledTask task) {
            this.task.set(checkNotNull(task, "task"));
        }

        ScheduledTask get() {
            return checkNotNull(task.get(), "task not initialized");
        }
    }
}