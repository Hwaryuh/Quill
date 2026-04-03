package io.quill.paper.player;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

final class PersistentFlags extends PersistentStorage {
    PersistentFlags(Player player) {
        super(player, "flag");
    }

    boolean get(String key) {
        return pdc().getOrDefault(key(key), PersistentDataType.BYTE, (byte) 0) == 1;
    }

    void set(String key, boolean value) {
        if (value) {
            pdc().set(key(key), PersistentDataType.BYTE, (byte) 1);
        } else {
            removeKey(key);
        }
    }

    void toggle(String key) {
        set(key, !get(key));
    }

    void remove(String key) {
        removeKey(key);
    }
}