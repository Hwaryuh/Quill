package io.quill.paper.player;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;

import java.util.Map;

final class Cooldowns {
    private final Map<String, Long> data = Maps.newHashMap();

    boolean isActive(String key) {
        Long endTick = data.get(key);
        if (endTick == null) return false;

        long currentTick = getCurrentTick();
        if (currentTick < endTick) {
            return true;
        }

        data.remove(key);
        return false;
    }

    void set(String key, long durationTicks) {
        data.put(key, getCurrentTick() + durationTicks);
    }

    void remove(String key) {
        data.remove(key);
    }

    long getRemaining(String key) {
        Long endTick = data.get(key);
        if (endTick == null) return 0;

        long remaining = endTick - getCurrentTick();
        return Math.max(0, remaining);
    }

    private long getCurrentTick() {
        return Bukkit.getServer().getCurrentTick();
    }
}