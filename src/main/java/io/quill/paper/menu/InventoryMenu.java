package io.quill.paper.menu;

import io.quill.paper.menu.button.ButtonBuilder;
import io.quill.paper.menu.button.DynamicButton;
import io.quill.paper.menu.button.InventoryButton;
import io.quill.paper.menu.internal.AdvancedSlotFilterButton;
import io.quill.paper.menu.internal.ButtonRegistry;
import io.quill.paper.menu.internal.ClickHandler;
import io.quill.paper.menu.internal.DragController;
import io.quill.paper.menu.internal.MenuView;
import io.quill.paper.menu.slot.AdvancedSlotFilter;
import io.quill.paper.menu.slot.PlaceholderSlot;
import io.quill.paper.menu.slot.SlotFilter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"UnstableApiUsage"})
public abstract class InventoryMenu {
    protected final Player player;
    protected InventoryView view;

    private final MenuView menuView;
    private final ButtonRegistry buttons;
    private final DragController dragController;
    private final ClickHandler clickHandler;

    protected InventoryMenu(Player player, MenuType.Typed<?, ?> menuType, Component title) {
        this.player = player;
        this.menuView = new MenuView(player, menuType, title);
        this.view = menuView.getView();
        this.buttons = new ButtonRegistry(menuView.getInventory());
        this.dragController = new DragController();
        this.clickHandler = new ClickHandler(buttons, player);
    }

    // Public

    protected final void initialize() {
        render();
    }

    public void open() {
        this.initialize();
        MenuManager.getInstance().register(this);
        view.open();
    }

    public void close() {
        player.closeInventory();
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryView getView() {
        return view;
    }

    public Inventory getInventory() {
        return menuView.getInventory();
    }

    // Protected

    protected ButtonBuilder button() {
        return new ButtonBuilder();
    }

    protected ButtonBuilder button(ItemStack icon) {
        return new ButtonBuilder().icon(icon);
    }

    protected void setItem(int slot, ItemStack item) {
        buttons.buttons.setItem(slot, item);
    }

    protected void setSlotEmpty(int slot) {
        buttons.buttons.setSlotEmpty(slot);
    }

    protected final void setSlotEmpty(int... slots) {
        buttons.buttons.setSlotEmpty(slots);
    }

    protected void setButton(int slot, InventoryButton button) {
        buttons.buttons.setButton(slot, button);
    }

    protected void removeButton(int slot) {
        buttons.buttons.removeButton(slot);
    }

    protected void removeButtons(int... slots) {
        buttons.buttons.removeButtons(slots);
    }

    protected void setPlayerInventoryButton(int slot, InventoryButton button) {
        buttons.playerInventory.setButton(slot, button);
    }

    protected void onPlayerInventory(Consumer<ButtonBuilder> configurator) {
        buttons.playerInventory.setFallback(configurator);
    }

    protected void setSlotFilter(int slot, Predicate<ItemStack> filter) {
        buttons.buttons.setSlotFilter(slot, filter);
    }

    protected void setSlotFilter(int slot, SlotFilter filter) {
        buttons.buttons.setSlotFilter(slot, filter);
    }

    protected void setAdvancedSlotFilter(int slot, AdvancedSlotFilter filter) {
        buttons.buttons.setAdvancedSlotFilter(slot, filter);
    }

    protected void setDynamicButton(int slot, DynamicButton button) {
        buttons.buttons.setDynamicButton(slot, button);
    }

    protected void setPlaceholderSlot(int slot, ItemStack placeholder, Predicate<ItemStack> itemFilter) {
        buttons.buttons.setPlaceholderSlot(slot, placeholder, itemFilter, null);
    }

    protected void setPlaceholderSlot(int slot, ItemStack placeholder, Predicate<ItemStack> itemFilter, int maxAmount) {
        buttons.buttons.setPlaceholderSlot(slot, placeholder, itemFilter, maxAmount);
    }

    protected void setStatefulSlot(int slot, Supplier<InventoryButton> buttonSupplier) {
        buttons.buttons.setStatefulSlot(slot, buttonSupplier);
    }

    protected void setDragPolicy(DragPolicy policy) {
        dragController.setPolicy(policy);
    }

    protected void onDrag(Predicate<Set<Integer>> checker) {
        dragController.setCustomChecker(checker);
    }

    protected void setTitle(Component title) {
        menuView.setTitle(title);
    }

    protected boolean tryShiftClickToSlot(ItemStack clicked, int targetSlot) {
        InventoryButton button = buttons.buttons.getButton(targetSlot);

        if (button instanceof AdvancedSlotFilterButton filterButton) {
            return filterButton.getFilter().handleShiftClick(clicked, getInventory(), targetSlot);
        }

        return false;
    }

    protected void routeShiftClickToFirst(Predicate<ItemStack> matcher, int... targetSlots) {
        buttons.shiftClicks.addRouter(matcher, targetSlots);
    }

    protected void routeShiftClickToFirst(Predicate<ItemStack> matcher, IntSupplier... targetSlotSuppliers) {
        buttons.shiftClicks.addRouter(matcher, targetSlotSuppliers);
    }

    protected void registerInputSlot(int slot) {
        buttons.inputSlots.register(slot);
    }

    protected void returnAllInputSlots() {
        for (int slot : buttons.inputSlots.getInputSlots()) {
            ItemStack item = getInventory().getItem(slot);
            if (item != null && !item.getType().isAir()) {
                InventoryButton button = buttons.buttons.getButton(slot);
                if (button instanceof AdvancedSlotFilterButton filterButton) {
                    if (filterButton.getFilter() instanceof PlaceholderSlot placeholderSlot) {
                        if (!placeholderSlot.isPlaceholder(item)) {
                            returnItem(item);
                            setSlotEmpty(slot);
                            placeholderSlot.showPlaceholder();
                        }
                        continue;
                    }
                }
                returnItem(item);
                setSlotEmpty(slot);
            }
        }
    }

    private void returnItem(ItemStack item) {
        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(item);
        if (!remaining.isEmpty()) {
            for (ItemStack drop : remaining.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
    }

    // Event

    void onClick(InventoryClickEvent event) {
        clickHandler.handle(event);
    }

    void onPlayerInventoryClick(InventoryClickEvent event) {
        clickHandler.handlePlayerInventory(event);
    }

    boolean shouldCancelDrag(InventoryDragEvent event) {
        return dragController.shouldCancel(event, getInventory().getSize());
    }

    void reopen() {
        menuView.reopen();
        this.view = menuView.getView();
    }

    // Lifecycle

    protected boolean canClose(InventoryCloseEvent event) {
        return true;
    }

    protected void onClose(InventoryCloseEvent event) { }

    protected abstract void render();
}