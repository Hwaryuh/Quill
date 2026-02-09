package io.quill.paper.event.bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public enum ArmorType {
    HELMET(5, "_HELMET", "_SKULL", "PLAYER_HEAD") {
        @Override
        public ItemStack getCurrent(Player player) {
            return player.getInventory().getHelmet();
        }

        @Override
        public void setCurrent(Player player, ItemStack item) {
            player.getInventory().setHelmet(item);
        }
    },
    CHESTPLATE(6, "_CHESTPLATE", "ELYTRA") {
        @Override
        public ItemStack getCurrent(Player player) {
            return player.getInventory().getChestplate();
        }

        @Override
        public void setCurrent(Player player, ItemStack item) {
            player.getInventory().setChestplate(item);
        }
    },
    LEGGINGS(7, "_LEGGINGS") {
        @Override
        public ItemStack getCurrent(Player player) {
            return player.getInventory().getLeggings();
        }

        @Override
        public void setCurrent(Player player, ItemStack item) {
            player.getInventory().setLeggings(item);
        }
    },
    BOOTS(8, "_BOOTS") {
        @Override
        public ItemStack getCurrent(Player player) {
            return player.getInventory().getBoots();
        }

        @Override
        public void setCurrent(Player player, ItemStack item) {
            player.getInventory().setBoots(item);
        }
    };

    private final int slot;
    private final Set<String> suffixes;
    private static final ArmorType[] CACHED_VALUES = values();

    ArmorType(int slot, String... suffixes) {
        this.slot = slot;
        this.suffixes = Set.of(suffixes);
    }

    public abstract ItemStack getCurrent(Player player);
    public abstract void setCurrent(Player player, ItemStack item);

    public static ArmorType matchType(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        String typeName = itemStack.getType().name();
        for (ArmorType armorType : CACHED_VALUES) {
            for (String suffix : armorType.suffixes) {
                if (typeName.endsWith(suffix)) {
                    return armorType;
                }
            }
        }
        return null;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isEmpty(Player player) {
        ItemStack current = getCurrent(player);
        return current == null || current.getType() == Material.AIR;
    }
}