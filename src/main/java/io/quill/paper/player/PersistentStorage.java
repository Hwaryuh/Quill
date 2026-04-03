package io.quill.paper.player;

import io.quill.paper.util.bukkit.pdc.PDCKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

abstract class PersistentStorage {
    protected static final String PDC_PREFIX_BASE = "pdata_";
    protected final Player player;
    protected final String prefix;

    protected PersistentStorage(Player player, String prefix) {
        this.player = player;
        this.prefix = PDC_PREFIX_BASE + prefix + "_";
    }

    protected PersistentDataContainer pdc() {
        return player.getPersistentDataContainer();
    }

    protected NamespacedKey key(String key) {
        return PDCKeys.of(prefix + key);
    }

    protected void removeKey(String key) {
        pdc().remove(key(key));
    }
}