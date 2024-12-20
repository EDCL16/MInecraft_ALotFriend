package com.edcl.alotfriendmod.entity.custom;

import com.edcl.alotfriendmod.entity.client.FriendRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FriendEntity extends PathfinderMob {

    private final SimpleContainer inventory = new SimpleContainer(10);
    private static final float MOVEMENT_SPEED =0.5f;

    public FriendEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.selectedTexture = FriendRenderer.GetRandomTexture();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 自訂 AI 行為，例如讓 NPC 跟隨玩家
        this.goalSelector.addGoal(1,new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowPlayerGoal(this, MOVEMENT_SPEED, 2f));
        this.goalSelector.addGoal(3,new LookAtPlayerGoal(this, Player.class,1.2f));
        //UseItemGoal
        this.goalSelector.addGoal(4,new RandomStrollGoal(this,1.1f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));


        //DoorInteractGoal
        //MeleeAttackGoal
        //RangedBowAttackGoal
        //InteractGoal
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ARMOR_TOUGHNESS, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1f)
                .add(Attributes.ATTACK_SPEED, 2)
                .add(Attributes.FLYING_SPEED,0.01f);
    }

    @Override
    public void dropFromLootTable(DamageSource damageSource, boolean causedByPlayer) {
        super.dropFromLootTable(damageSource, causedByPlayer);

        // 添加自訂掉落物
        if (!this.level().isClientSide) {
            ItemStack dropItem = new ItemStack(Items.DIAMOND); // 替換為你的掉落物品
            this.spawnAtLocation(dropItem, 1.0F); // 生成掉落物
        }
    }

    private String selectedTexture;
    public String getSelectedTexture() {
        return selectedTexture;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SelectedTexture", this.selectedTexture);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SelectedTexture")) {
            this.selectedTexture = compound.getString("SelectedTexture");
        }
    }
}
