package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class EatFoodGoal extends Goal {
    private final FriendEntity friendEntity;
    private int cooldown = 0; // 用於避免頻繁檢查

    public EatFoodGoal(FriendEntity friendEntity) {
        this.friendEntity = friendEntity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); // 添加行為標誌
    }

    @Override
    public boolean canUse() {
        // 只有當實體飢餓時才能使用此目標
        return friendEntity.isHungry() && hasFoodInInventory();
    }

    @Override
    public void start() {
        super.start();
        consumeFoodFromInventory();
    }

    @Override
    public boolean canContinueToUse() {
        // 如果不再飢餓，停止目標
        return friendEntity.isHungry() && cooldown <= 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (cooldown > 0) cooldown--;
    }

    private boolean hasFoodInInventory() {
        // 檢查背包中是否有可食用物品
        for (int i = 0; i < friendEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = friendEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && itemStack.isEdible()) {
                return true;
            }
        }
        return false;
    }

    private void consumeFoodFromInventory() {
        // 消耗背包中的食物
        for (int i = 0; i < friendEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = friendEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && itemStack.isEdible()) {
                friendEntity.eatFood(itemStack);
                System.out.println("eat : "+itemStack);
                cooldown = 20; // 設置冷卻時間，避免頻繁進食
                break;
            }
        }
    }
}
