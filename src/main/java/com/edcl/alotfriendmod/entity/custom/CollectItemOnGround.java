package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class CollectItemOnGround extends Goal {
    private final Mob mob;
    private ItemEntity targetItem; // 目標掉落物
    private static final int SEARCH_RADIUS = 15; // 搜索範圍
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier = 1.0D; // 移動速度

    public CollectItemOnGround(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 查找附近是否有掉落物
        List<ItemEntity> nearbyItems = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(SEARCH_RADIUS));

        if (!nearbyItems.isEmpty()) {
            // 根據距離選擇最接近的掉落物
            targetItem = nearbyItems.stream()
                    .min((item1, item2) -> Double.compare(item1.distanceToSqr(mob), item2.distanceToSqr(mob)))
                    .orElse(null);

            if (targetItem != null) {
                Vec3 itemPosition = targetItem.position();
                wantedX = itemPosition.x;
                wantedY = itemPosition.y;
                wantedZ = itemPosition.z;
                return true; // 找到掉落物且設定成功
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        // 如果導航未完成且目標仍然存在
        return targetItem != null && targetItem.isAlive() && !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        // 開始導航到目標掉落物
        if (targetItem != null) {
            mob.getNavigation().moveTo(wantedX, wantedY, wantedZ, speedModifier);
        }
    }

    @Override
    public void tick() {
        if (targetItem != null) {
            double distance = mob.distanceTo(targetItem);

            // 當實體靠近掉落物時，撿取掉落物
            if (distance < 1.0) {
                if(mob instanceof  FriendEntity)
                {
                    FriendEntity friend = (FriendEntity)mob;
                    friend.pickUp(targetItem);
                }
                targetItem = null;
            }
        }
    }

    @Override
    public void stop() {
        targetItem = null;
    }
}
