package io.quill.paper.menu.internal;

import io.quill.paper.menu.internal.manger.ButtonManager;
import io.quill.paper.menu.internal.manger.InputSlotManager;
import io.quill.paper.menu.internal.manger.PlayerInventoryManager;
import io.quill.paper.menu.internal.manger.ShiftClickManager;
import org.bukkit.inventory.Inventory;

public class ButtonRegistry {
    public final ButtonManager buttons;
    public final PlayerInventoryManager playerInventory;
    public final ShiftClickManager shiftClicks;
    public final InputSlotManager inputSlots;

    public ButtonRegistry(Inventory inventory) {
        this.buttons = new ButtonManager(inventory);
        this.playerInventory = new PlayerInventoryManager();
        this.shiftClicks = new ShiftClickManager();
        this.inputSlots = new InputSlotManager();
    }
}