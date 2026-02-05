package io.quill.paper.util.bukkit;

import java.time.Duration;

public final class Ticks {
    private Ticks() { }

    private static final int TICKS_PER_SECOND = 20;
    private static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    private static final int TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;

    public static long sec2Ticks(long sec) {
        return sec * TICKS_PER_SECOND;
    }

    public static int sec2Ticks(double sec) {
        return Math.toIntExact(Math.round(sec * TICKS_PER_SECOND));
    }

    public static long min2Ticks(long min) {
        return min * TICKS_PER_MINUTE;
    }

    public static int min2Ticks(int min) {
        return min * TICKS_PER_MINUTE;
    }

    public static long hours2Ticks(long h) {
        return h * TICKS_PER_HOUR;
    }

    public static int hours2Ticks(int h) {
        return h * TICKS_PER_HOUR;
    }

    public static long duration2Ticks(Duration d) {
        return d.getSeconds() * TICKS_PER_SECOND;
    }

    public static double ticks2Sec(long t) {
        return (double) t / TICKS_PER_SECOND;
    }

    public static long ticks2Min(long t) {
        return t / TICKS_PER_MINUTE;
    }

    public static long ticks2Hours(long t) {
        return t / TICKS_PER_HOUR;
    }

    public static Duration ticks2Duration(long t) {
        return Duration.ofSeconds(t / TICKS_PER_SECOND);
    }
}