package io.quill.paper.player;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class PlayerContext {
    private final UUID uuid;
    private final Player player;

    private Cooldowns cooldowns;
    private Flags flags;
    private Counters counters;
    private Metadata metadata;
    private PersistentFlags persistentFlags;

    PlayerContext(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
    }

    public UUID uuid() {
        return uuid;
    }

    public Player player() {
        return player;
    }

    // === Cooldown ===

    public boolean cooldown(String key) {
        return cooldowns().isActive(key);
    }

    public PlayerContext cooldown(String key, long ticks) {
        cooldowns().set(key, ticks);
        return this;
    }

    public long cooldownRemaining(String key) {
        return cooldowns().getRemaining(key);
    }

    public PlayerContext removeCooldown(String key) {
        cooldowns().remove(key);
        return this;
    }

    private Cooldowns cooldowns() {
        if (cooldowns == null) {
            cooldowns = new Cooldowns();
        }
        return cooldowns;
    }

    // === Flag ===

    public boolean flag(String key) {
        return flags().has(key);
    }

    public PlayerContext flag(String key, boolean value) {
        if (value) {
            flags().set(key);
        } else {
            flags().remove(key);
        }
        return this;
    }

    public PlayerContext toggleFlag(String key) {
        flags().toggle(key);
        return this;
    }

    public PlayerContext removeFlag(String key) {
        flags().remove(key);
        return this;
    }

    private Flags flags() {
        if (flags == null) {
            flags = new Flags();
        }
        return flags;
    }

    // === Counter ===

    public int counter(String key) {
        return counters().get(key);
    }

    public PlayerContext counter(String key, int value) {
        counters().set(key, value);
        return this;
    }

    public int increment(String key) {
        return counters().increment(key);
    }

    public int decrement(String key) {
        return counters().decrement(key);
    }

    public PlayerContext resetCounter(String key) {
        counters().reset(key);
        return this;
    }

    private Counters counters() {
        if (counters == null) {
            counters = new Counters(player);
        }
        return counters;
    }

    // === Persistent Flag ===

    public boolean persistentFlag(String key) {
        return persistentFlags().get(key);
    }

    public PlayerContext persistentFlag(String key, boolean value) {
        persistentFlags().set(key, value);
        return this;
    }

    public PlayerContext togglePersistentFlag(String key) {
        persistentFlags().toggle(key);
        return this;
    }

    public PlayerContext removePersistentFlag(String key) {
        persistentFlags().remove(key);
        return this;
    }

    private PersistentFlags persistentFlags() {
        if (persistentFlags == null) {
            persistentFlags = new PersistentFlags(player);
        }
        return persistentFlags;
    }

    // === Metadata ===

    public <T> Optional<T> metadata(String key, Class<T> type) {
        return metadata().get(key, type);
    }

    public PlayerContext metadata(String key, Object value) {
        metadata().set(key, value);
        return this;
    }

    public PlayerContext removeMetadata(String key) {
        metadata().remove(key);
        return this;
    }

    public boolean hasMetadata(String key) {
        return metadata().has(key);
    }

    private Metadata metadata() {
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    void clear() {
        // 휘발성 데이터 제거 (Counters 객체는 휘발성, PDC 데이터는 영속성 유지)
        cooldowns = null;
        flags = null;
        counters = null;
        metadata = null;
    }
}