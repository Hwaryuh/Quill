package io.quill.paper.player;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

final class Counters extends PersistentStorage {
    Counters(Player player) {
        super(player, "counter");
    }

    int get(String key) {
        return pdc().getOrDefault(key(key), PersistentDataType.INTEGER, 0);
    }

    void set(String key, int value) {
        pdc().set(key(key), PersistentDataType.INTEGER, value);
    }

    int increment(String key) {
        int newValue = get(key) + 1;
        set(key, newValue);
        return newValue;
    }

    int decrement(String key) {
        int newValue = get(key) - 1;
        set(key, newValue);
        return newValue;
    }

    void reset(String key) {
        removeKey(key);
    }
}