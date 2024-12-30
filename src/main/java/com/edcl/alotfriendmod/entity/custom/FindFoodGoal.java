package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class FindFoodGoal extends Goal {

    private final FriendEntity friendEntity;
    private ItemEntity targetFood; // 目標食物
    private static final double SEARCH_RADIUS = 10.0;

    public FindFoodGoal(FriendEntity friendEntity) {
        this.friendEntity = friendEntity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 當實體飢餓時啟動目標
        return friendEntity.isHungry();
    }

    @Override
    public boolean canContinueToUse() {
        // 當目標食物仍然存在且實體還需要進食時繼續執行
        return targetFood != null && !targetFood.isRemoved() && friendEntity.isHungry();
    }

    @Override
    public void start() {
        // 在範圍內找到最近的食物
        this.targetFood = findClosestFood();
    }

    @Override
    public void tick() {
        if (targetFood != null) {
            // 移動到目標食物
            friendEntity.getNavigation().moveTo(targetFood, 1.0);

            // 當靠近食物時拾取並食用
            if (friendEntity.distanceTo(targetFood) < 2.0) {
                ItemStack foodItem = targetFood.getItem();
                friendEntity.eatFood(foodItem);
                targetFood.discard(); // 移除已拾取的食物
                this.targetFood = null; // 重置目標
            }
        }
    }

    @Override
    public void stop() {
        this.targetFood = null; // 停止時重置目標
    }

    private ItemEntity findClosestFood() {
        return friendEntity.getLevel().getEntitiesOfClass(ItemEntity.class,
                        friendEntity.getBoundingBox().inflate(SEARCH_RADIUS),
                        item -> item.getItem().isEdible())
                .stream()
                .min((a, b) -> Double.compare(friendEntity.distanceTo(a), friendEntity.distanceTo(b)))
                .orElse(null);
    }
}
