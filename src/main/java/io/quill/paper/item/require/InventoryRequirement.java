package io.quill.paper.item.require;

import org.bukkit.inventory.PlayerInventory;

public interface InventoryRequirement {
    /**
     * 인벤토리가 요구사항을 만족하는지 검증
     */
    boolean test(PlayerInventory inventory);

    /**
     * 소비 시도 (간단한 성공/실패만 필요할 때)
     * @return 성공 여부
     */
    default boolean consume(PlayerInventory inventory) {
        return tryConsume(inventory).isSuccess();
    }

    /**
     * 소비 시도 with 상세 결과 (실패 이유가 필요할 때)
     * @return 소비 결과
     */
    ConsumeResult tryConsume(PlayerInventory inventory);

    /**
     * AND 조건 조합
     */
    default InventoryRequirement and(InventoryRequirement other) {
        return new CompositeRequirement(this, other, CompositeRequirement.Type.AND);
    }

    /**
     * OR 조건 조합
     */
    default InventoryRequirement or(InventoryRequirement other) {
        return new CompositeRequirement(this, other, CompositeRequirement.Type.OR);
    }
}