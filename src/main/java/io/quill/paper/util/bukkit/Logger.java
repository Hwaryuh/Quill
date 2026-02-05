package io.quill.paper.util.bukkit;

import io.quill.paper.Quill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Logger {
    private static ComponentLogger internalLogger;
    private static final JavaPlugin PLUGIN = Quill.getInstance().getPlugin();

    private Logger() { }

    private static @NotNull ComponentLogger getLogger() {
        if (internalLogger != null) return internalLogger;

        synchronized (PLUGIN) {
            if (internalLogger != null) return internalLogger;
            return internalLogger = ComponentLogger.logger(PLUGIN.getLogger().getName());
        }
    }

    public static void info(@NotNull Component... msg) {
        synchronized (PLUGIN) {
            for (Component m : msg) {
                getLogger().info(m);
            }
        }
    }

    public static void warn(@NotNull Component... msg) {
        synchronized (PLUGIN) {
            for (Component m : msg) {
                getLogger().warn(m);
            }
        }
    }

    public static void error(@NotNull Component... msg) {
        synchronized (PLUGIN) {
            for (Component m : msg) {
                getLogger().error(m);
            }
        }
    }

    public static void info(@NotNull String... msg) {
        info(toComponents(msg));
    }

    public static void warn(@NotNull String... msg) {
        warn(toComponents(msg));
    }

    public static void error(@NotNull String... msg) {
        error(toComponents(msg));
    }

    public static void info(@NotNull String msg, @NotNull Throwable throwable) {
        synchronized (PLUGIN) {
            getLogger().info(Component.text(msg));
            logThrowable(throwable, LogLevel.INFO);
        }
    }

    public static void warn(@NotNull String msg, @NotNull Throwable throwable) {
        synchronized (PLUGIN) {
            getLogger().warn(Component.text(msg));
            logThrowable(throwable, LogLevel.WARN);
        }
    }

    public static void error(@NotNull String msg, @NotNull Throwable throwable) {
        synchronized (PLUGIN) {
            getLogger().error(Component.text(msg));
            logThrowable(throwable, LogLevel.ERROR);
        }
    }

    private static Component[] toComponents(String... msg) {
        Component[] components = new Component[msg.length];
        for (int i = 0; i < msg.length; i++) {
            components[i] = Component.text(msg[i]);
        }
        return components;
    }

    private static void log(LogLevel level, Component message) {
        switch (level) {
            case INFO -> getLogger().info(message);
            case WARN -> getLogger().warn(message);
            case ERROR -> getLogger().error(message);
        }
    }

    private static void logThrowable(@NotNull Throwable throwable, LogLevel level) {
        log(level, Component.text(throwable.getClass().getName() + ": " + throwable.getMessage()));

        for (StackTraceElement element : throwable.getStackTrace()) {
            log(level, Component.text("  at " + element.toString()));
        }

        if (throwable.getCause() != null) {
            log(level, Component.text("Caused by:"));
            logThrowable(throwable.getCause(), level);
        }
    }

    private enum LogLevel {
        INFO, WARN, ERROR
    }
}