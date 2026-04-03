package io.quill.paper.item;

import org.bukkit.inventory.ItemStack;

public final class ItemMatcher {
    public static boolean matches(ItemStack item, ItemStack template) {
        if (item == null || template == null) return false;
        if (item.getType() != template.getType()) return false;
        if (!item.hasItemMeta() || !template.hasItemMeta()) return false;

        var itemMeta = item.getItemMeta();
        var templateMeta = template.getItemMeta();

        Integer itemCmd = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : null;
        Integer templateCmd = templateMeta.hasCustomModelData() ? templateMeta.getCustomModelData() : null;

        return itemCmd != null && itemCmd.equals(templateCmd);
    }

    public static boolean matchesMaterial(ItemStack item, ItemStack template) {
        if (item == null || template == null) return false;
        return item.getType() == template.getType();
    }
}
