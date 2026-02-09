package io.quill.paper.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Set;

import static io.quill.paper.event.bukkit.ArmorEquipEventDispatcher.fireAndCheckCancel;
import static io.quill.paper.event.bukkit.ArmorEquipEventDispatcher.isAirOrNull;

final class ArmorInteractListener implements Listener {
    private static final Set<String> BLOCKED_PATTERNS = Set.of(
            "FURNACE", "CHEST", "TRAPPED_CHEST", "BEACON", "DISPENSER", "DROPPER",
            "HOPPER", "CRAFTING_TABLE", "ENCHANTING_TABLE", "ENDER_CHEST",
            "BREWING_STAND", "CAULDRON", "DRAGON_EGG", "LEVER", "DAYLIGHT_DETECTOR",
            "REPEATER", "COMPARATOR",

            "*_DOOR", "*_FENCE_GATE", "*_FENCE", "*_BUTTON", "*_TRAPDOOR",
            "*_SIGN", "*_WALL_SIGN", "*_SHULKER_BOX", "ANVIL*", "*CAULDRON"
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.useItemInHand() == Event.Result.DENY) return;
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = e.getPlayer();

        if (isBlocked(e, player)) return;

        ArmorType type = ArmorType.matchType(e.getItem());
        if (type != null && type.isEmpty(player)) {
            if (fireAndCheckCancel(player, ArmorEquipEvent.EquipMethod.HOTBAR,
                    type, null, e.getItem())) {
                e.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.matchType(e.getBrokenItem());
        if (type == null) return;

        Player player = e.getPlayer();
        if (fireAndCheckCancel(player, ArmorEquipEvent.EquipMethod.BROKE,
                type, e.getBrokenItem(), null)) {

            ItemStack repaired = e.getBrokenItem().clone();
            repaired.setAmount(1);
            repaired.editMeta(meta -> {
                if (meta instanceof Damageable damageable) {
                    damageable.setDamage(Math.max(0, damageable.getDamage() - 1));
                }
            });

            type.setCurrent(player, repaired);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getKeepInventory()) return;

        Player player = e.getEntity();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (!isAirOrNull(armor)) {
                fireAndCheckCancel(player, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(armor), armor, null);
            }
        }
    }

    @EventHandler
    public void onDispenseArmor(BlockDispenseArmorEvent e) {
        if (!(e.getTargetEntity() instanceof Player player)) return;

        ArmorType type = ArmorType.matchType(e.getItem());
        if (type != null) {
            if (fireAndCheckCancel(player, ArmorEquipEvent.EquipMethod.DISPENSER, type, null, e.getItem())) {
                e.setCancelled(true);
            }
        }
    }

    private static boolean isBlocked(PlayerInteractEvent e, Player player) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                e.useInteractedBlock() != Event.Result.DENY &&
                e.getClickedBlock() != null &&
                !player.isSneaking()) {

            return matches(e.getClickedBlock().getType().name());
        }
        return false;
    }

    private static boolean matches(String materialName) {
        for (String pattern : BLOCKED_PATTERNS) {
            if (pattern.startsWith("*") && pattern.endsWith("*")) {
                // *ABC* → contains
                String substring = pattern.substring(1, pattern.length() - 1);
                if (materialName.contains(substring)) return true;
            } else if (pattern.startsWith("*")) {
                // *ABC → endsWith
                if (materialName.endsWith(pattern.substring(1))) return true;
            } else if (pattern.endsWith("*")) {
                // ABC* → startsWith
                if (materialName.startsWith(pattern.substring(0, pattern.length() - 1))) return true;
            } else {
                // ABC → equals
                if (materialName.equals(pattern)) return true;
            }
        }
        return false;
    }
}