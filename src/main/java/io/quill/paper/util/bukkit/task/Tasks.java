package io.quill.paper.util.bukkit.task;

import io.quill.paper.Quill;
import io.quill.paper.util.bukkit.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Tasks {
    private static final JavaPlugin PLUGIN = Quill.getInstance().getPlugin();

    private Tasks() { }

    public static void run(Runnable task) {
        checkNotNull(task, "task");
        PLUGIN.getServer().getScheduler().runTask(PLUGIN, wrapSafe(task));
    }

    public static CancellableTask later(long delay, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");

        return wrap(PLUGIN.getServer().getScheduler().runTaskLater(PLUGIN, wrapSafe(task), delay), false);
    }

    public static CancellableTask repeat(long delay, long period, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        return wrap(PLUGIN.getServer().getScheduler().runTaskTimer(PLUGIN, wrapSafe(task), delay, period), true);
    }

    public static void runAsync(Runnable task) {
        checkNotNull(task, "task");
        PLUGIN.getServer().getScheduler().runTaskAsynchronously(PLUGIN, wrapSafe(task));
    }

    public static CancellableTask laterAsync(long delay, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");

        return wrap(PLUGIN.getServer().getScheduler().runTaskLaterAsynchronously(PLUGIN, wrapSafe(task), delay), false);
    }

    public static CancellableTask repeatAsync(long delay, long period, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        return wrap(PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(PLUGIN, wrapSafe(task), delay, period), true);
    }

    public static CancellableTask repeat(long delay, long period, Consumer<CancellableTask> task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        LazyTaskWrapper wrapper = new LazyTaskWrapper();
        BukkitTask bukkitTask = PLUGIN.getServer().getScheduler().runTaskTimer(
                PLUGIN,
                wrapSafe(() -> task.accept(wrapper)),
                delay,
                period
        );

        wrapper.initialize(bukkitTask, true);
        return wrapper;
    }

    public static CancellableTask repeat(long delay, long period, int times, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");
        checkArgument(times > 0, "times must be positive");

        if (times == 1) {
            return later(delay, task);
        }

        AtomicInteger count = new AtomicInteger(0);
        return repeat(delay, period, t -> {
            task.run();

            if (count.incrementAndGet() >= times) {
                t.cancel();
            }
        });
    }

    public static CancellableTask repeatAsync(long delay, long period, Consumer<CancellableTask> task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");

        LazyTaskWrapper wrapper = new LazyTaskWrapper();
        BukkitTask bukkitTask = PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(
                PLUGIN,
                wrapSafe(() -> task.accept(wrapper)),
                delay,
                period
        );

        wrapper.initialize(bukkitTask, true);
        return wrapper;
    }

    public static CancellableTask repeatAsync(long delay, long period, int times, Runnable task) {
        checkNotNull(task, "task");
        checkArgument(delay >= 0, "delay must be non-negative");
        checkArgument(period > 0, "period must be positive");
        checkArgument(times > 0, "times must be positive");

        AtomicInteger remaining = new AtomicInteger(times);

        return repeatAsync(delay, period, t -> {
            if (remaining.getAndDecrement() <= 0) {
                t.cancel();
                return;
            }
            task.run();
        });
    }

    private static Runnable wrapSafe(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Throwable t) {
                Logger.error("Exception in scheduled task", t);
            }
        };
    }

    private static CancellableTask wrap(BukkitTask task, boolean repeating) {
        return new BukkitTaskWrapper(task, repeating);
    }

    /**
     * BukkitTask가 생성된 후 초기화되는 지연 래퍼.
     * delay=0인 반복 태스크의 타이밍 이슈를 해결한다.
     */
    private static final class LazyTaskWrapper implements BukkitBackedTask {
        private volatile TaskState state;

        void initialize(BukkitTask task, boolean repeat) {
            this.state = new TaskState(checkNotNull(task, "task"), repeat);
        }

        @Override
        public boolean cancel() {
            TaskState s = state;
            if (s == null || s.task().isCancelled()) {
                return false;
            }
            s.task().cancel();
            return true;
        }

        @Override
        public boolean isCancelled() {
            TaskState s = state;
            return s != null && s.task().isCancelled();
        }

        @Override
        public int getTaskId() {
            TaskState s = state;
            return s != null ? s.task().getTaskId() : -1;
        }

        @Override
        public boolean isRepeating() {
            TaskState s = state;
            return s != null && s.repeat();
        }

        private record TaskState(BukkitTask task, boolean repeat) { }
    }
}