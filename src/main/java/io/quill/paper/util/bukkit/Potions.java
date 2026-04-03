package io.quill.paper.util.bukkit;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potions {
    public static PotionEffectBuilder of(PotionEffectType type) {
        return new PotionEffectBuilder(type);
    }

    public static class PotionEffectBuilder {
        private final PotionEffectType type;
        private int duration;
        private int amplifier = 0;
        private boolean ambient = false;
        private boolean particles = true;
        private boolean icon = true;

        private PotionEffectBuilder(PotionEffectType type) {
            Preconditions.checkNotNull(type, "PotionEffectType cannot be null");
            this.type = type;
        }

        public PotionEffectBuilder minutes(int minutes) {
            Preconditions.checkArgument(minutes > 0, "Minutes must be positive, got: %s", minutes);
            this.duration = Ticks.min2Ticks(minutes);
            return this;
        }

        public PotionEffectBuilder seconds(double seconds) {
            Preconditions.checkArgument(seconds > 0, "Seconds must be positive, got: %s", seconds);
            this.duration = Ticks.sec2Ticks(seconds);
            return this;
        }

        public PotionEffectBuilder duration(int ticks) {
            Preconditions.checkArgument(ticks > 0, "Duration must be positive, got: %s", ticks);
            this.duration = ticks;
            return this;
        }

        public PotionEffectBuilder level(int level) {
            Preconditions.checkArgument(level >= 1, "Level must be at least 1, got: %s", level);
            this.amplifier = level - 1;
            return this;
        }

        public PotionEffectBuilder ambient(boolean ambient) {
            this.ambient = ambient;
            return this;
        }

        public PotionEffectBuilder particles(boolean particles) {
            this.particles = particles;
            return this;
        }

        public PotionEffectBuilder icon(boolean icon) {
            this.icon = icon;
            return this;
        }

        public PotionEffect build() {
            Preconditions.checkState(duration > 0, "Duration must be set before building");
            return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
        }

        public void applyTo(Player player) {
            Preconditions.checkNotNull(player, "Player cannot be null");
            player.addPotionEffect(build());
        }

        public void applyTo(Player... players) {
            Preconditions.checkNotNull(players, "Players array cannot be null");
            PotionEffect effect = build();
            for (Player player : players) {
                if (player != null) {
                    player.addPotionEffect(effect);
                }
            }
        }
    }
}