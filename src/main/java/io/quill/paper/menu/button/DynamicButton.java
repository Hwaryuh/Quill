package io.quill.paper.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface DynamicButton extends InventoryButton {
    /**
     * 클릭 시 표시할 아이템을 생성
     */
    ItemStack createIcon(ClickContext context);

    /**
     * 클릭 이벤트 처리 (ClickContext 사용)
     */
    void onClick(ClickContext context);

    /**
     * 이벤트 취소 여부 (ClickContext 사용)
     * InventoryButton의 shouldCancel(InventoryClickEvent)를 구현하는 용도
     */
    boolean shouldCancel(ClickContext context);

    @Override
    default ItemStack getIcon() {
        return null;
    }

    @Override
    default void onClick(InventoryClickEvent event) {
        onClick(new ClickContext(event));
    }

    @Override
    default boolean shouldCancel(InventoryClickEvent event) {
        return shouldCancel(new ClickContext(event));
    }
}