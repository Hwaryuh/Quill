package io.quill.paper.menu.button;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ButtonBuilder {
    private ItemStack icon;
    private final Map<ClickType, Consumer<ClickContext>> handlers = Maps.newHashMap();
    private Predicate<ClickContext> cancelCondition = ctx -> true;

    public ButtonBuilder icon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public ButtonBuilder onClick(ClickType type, Consumer<ClickContext> handler) {
        handlers.put(type, handler);
        return this;
    }

    public ButtonBuilder onLeftClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.LEFT, handler);
    }

    public ButtonBuilder onRightClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.RIGHT, handler);
    }

    public ButtonBuilder onShiftClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.SHIFT_LEFT, handler).onClick(ClickType.SHIFT_RIGHT, handler);
    }

    public ButtonBuilder onShiftLeftClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.SHIFT_LEFT, handler);
    }

    public ButtonBuilder onShiftRightClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.SHIFT_RIGHT, handler);
    }

    public ButtonBuilder onLeftBorderClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.WINDOW_BORDER_LEFT, handler);
    }

    public ButtonBuilder onRightBorderClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.WINDOW_BORDER_RIGHT, handler);
    }

    public ButtonBuilder onMiddleClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.MIDDLE, handler);
    }

    public ButtonBuilder onPressHotbar(Consumer<ClickContext> handler) {
        return onClick(ClickType.NUMBER_KEY, handler);
    }

    public ButtonBuilder onPressHotbar(int number, Consumer<ClickContext> handler) {
        if (number < 1 || number > 9) {
            throw new IllegalArgumentException("Number must be between 1 and 9");
        }

        return onClick(ClickType.NUMBER_KEY, ctx -> {
            if (ctx.event().getHotbarButton() == number - 1) {
                handler.accept(ctx);
            }
        });
    }

    public ButtonBuilder onDoubleClick(Consumer<ClickContext> handler) {
        return onClick(ClickType.DOUBLE_CLICK, handler);
    }

    public ButtonBuilder onPressDrop(Consumer<ClickContext> handler) {
        return onClick(ClickType.DROP, handler);
    }

    public ButtonBuilder onPressDropAll(Consumer<ClickContext> handler) {
        return onClick(ClickType.CONTROL_DROP, handler);
    }

    public ButtonBuilder onPressSwap(Consumer<ClickContext> handler) {
        return onClick(ClickType.SWAP_OFFHAND, handler);
    }

    public ButtonBuilder onAnyClick(Consumer<ClickContext> handler) {
        for (ClickType type : ClickType.values()) {
            handlers.put(type, handler);
        }
        return this;
    }

    // Cancel
    public ButtonBuilder cancelAll() {
        this.cancelCondition = ctx -> true;
        return this;
    }

    public ButtonBuilder cancelNone() {
        this.cancelCondition = ctx -> false;
        return this;
    }

    public ButtonBuilder cancelOn(ClickType... types) {
        this.cancelCondition = ctx -> {
            for (ClickType type : types) {
                if (ctx.event().getClick() == type) return true;
            }
            return false;
        };
        return this;
    }

    public ButtonBuilder cancelIf(Predicate<ClickContext> condition) {
        this.cancelCondition = condition;
        return this;
    }

    // Build
    public InventoryButton build() {
        return new InventoryButton() {
            @Override
            public ItemStack getIcon() {
                return icon;
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                ClickContext context = new ClickContext(event);
                Consumer<ClickContext> handler = handlers.get(event.getClick());
                if (handler != null) {
                    handler.accept(context);
                }
            }

            @Override
            public boolean shouldCancel(InventoryClickEvent event) {
                ClickContext context = new ClickContext(event);
                return cancelCondition.test(context);
            }
        };
    }
}