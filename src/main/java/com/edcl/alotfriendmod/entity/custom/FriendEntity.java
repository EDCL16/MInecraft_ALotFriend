package com.edcl.alotfriendmod.entity.custom;

import com.edcl.alotfriendmod.entity.client.FriendRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendEntity extends PathfinderMob implements InventoryCarrier, Npc, Merchant {

    private static final Logger log = LoggerFactory.getLogger(FriendEntity.class);
    private final SimpleContainer inventory = new SimpleContainer(100);
    public  static final float MOVEMENT_SPEED =0.5f;

    public FriendEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.selectedTexture = FriendRenderer.GetRandomTexture();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1,new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
        }));

        this.goalSelector.addGoal(3,new CollectItemOnGround(this));

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5,new StrollThroughVillageGoal(this,20));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, FriendEntity.class , 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ARMOR_TOUGHNESS, 0.25f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1f)
                .add(Attributes.ATTACK_SPEED, 2)
                .add(Attributes.FLYING_SPEED,0.01f)
                .add(Attributes.ATTACK_DAMAGE,3)
                .add(Attributes.ARMOR, 2.0D);

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
        compound.putBoolean("PersistenceRequired", true);
    }

    @Override
    public SimpleContainer getInventory() {
        return null;
    }

    @Override
    public void setTradingPlayer(@Nullable Player pTradingPlayer) {

    }

    @Override
    public @Nullable Player getTradingPlayer() {
        return null;
    }

    @Override
    public MerchantOffers getOffers() {
        return null;
    }

    @Override
    public void overrideOffers(MerchantOffers pOffers) {

    }

    @Override
    public void notifyTrade(MerchantOffer pOffer) {

    }

    @Override
    public void notifyTradeUpdated(ItemStack pStack) {

    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int pXp) {

    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    //private static FriendAnimationState animeState = FriendAnimationState.IDLE;
    @Override
    public void tick() {
        super.tick();
        if(attackAnim >0f)
        {
            this.attackAnimationState.animateWhen(!isInWaterOrBubble(), this.tickCount);
        }

        if(level().isClientSide())
        {
            this.idleAnimationState.animateWhen(!isInWaterOrBubble() && !walkAnimation.isMoving(), this.tickCount);
        }
    }

    public static boolean canSpawn(EntityType<FriendEntity> entityType, LevelAccessor world, MobSpawnType spawnType,
                                   BlockPos pos, RandomSource random) {
        if (!(world instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel) world;
        return checkMobSpawnRules(entityType, world, spawnType, pos, random)
                && serverLevel.isCloseToVillage(pos, 5);
    }

    @Override
    public boolean canPickUpLoot() {
        checkAndUpdateEquipment();
        return true;
    }

    public void checkAndUpdateEquipment() {
        // 檢查頭盔
        this.checkAndEquip(EquipmentSlot.HEAD);
        // 檢查胸甲
        this.checkAndEquip(EquipmentSlot.CHEST);
        // 檢查護腿
        this.checkAndEquip(EquipmentSlot.LEGS);
        // 檢查鞋子
        this.checkAndEquip(EquipmentSlot.FEET);
    }

    // 檢查特定位置的裝備
    private void checkAndEquip(EquipmentSlot slot) {
        ItemStack currentItem = this.getItemBySlot(slot);
        ItemStack bestItem = getBestItemForSlot(slot);

        if (isBetterEquipment(bestItem, currentItem)) {
            this.setItemSlot(slot, bestItem);  // 如果新裝備更好，替換當前裝備
        }
    }

    private ItemStack getBestItemForSlot(EquipmentSlot slot) {
        ItemStack bestItem = ItemStack.EMPTY;
        int bestArmorValue = -1;

        // 遍歷背包中的所有物品
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);

            // 只處理防具物品
            if (itemStack.getItem() instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem) itemStack.getItem();

                // 根據防具類型手動確定裝備的 EquipmentSlot
                EquipmentSlot itemSlot = getArmorSlotForItem(armorItem);

                // 如果裝備的槽位與指定的槽位一致，則進行比較
                if (itemSlot == slot) {
                    // 比較防護值
                    int armorValue = armorItem.getDefense();
                    if (armorValue > bestArmorValue) {
                        bestArmorValue = armorValue;
                        bestItem = itemStack;
                    }
                }
            }
        }

        return bestItem;
    }

    // 手動決定裝備對應的防具槽
    private EquipmentSlot getArmorSlotForItem(ArmorItem armorItem) {
        if (armorItem == Items.DIAMOND_HELMET || armorItem == Items.IRON_HELMET || armorItem == Items.GOLDEN_HELMET||armorItem == Items.LEATHER_HELMET) {
            return EquipmentSlot.HEAD;
        } else if (armorItem == Items.DIAMOND_CHESTPLATE || armorItem == Items.IRON_CHESTPLATE || armorItem == Items.GOLDEN_CHESTPLATE||armorItem == Items.LEATHER_CHESTPLATE) {
            return EquipmentSlot.CHEST;
        } else if (armorItem == Items.DIAMOND_LEGGINGS || armorItem == Items.IRON_LEGGINGS || armorItem == Items.GOLDEN_LEGGINGS||armorItem == Items.LEATHER_LEGGINGS) {
            return EquipmentSlot.LEGS;
        } else if (armorItem == Items.DIAMOND_BOOTS || armorItem == Items.IRON_BOOTS || armorItem == Items.GOLDEN_BOOTS||armorItem == Items.LEATHER_BOOTS) {
            return EquipmentSlot.FEET;
        }
        return EquipmentSlot.MAINHAND; // 預設返回錯誤槽位
    }

    // 比較裝備是否更好
    private boolean isBetterEquipment(ItemStack newItem, ItemStack currentItem) {
        if (currentItem.isEmpty()) {
            return true; // 如果目前沒有裝備，直接裝上新裝備
        }

        // 確保裝備是 ArmorItem
        if (!(newItem.getItem() instanceof ArmorItem) || !(currentItem.getItem() instanceof ArmorItem)) {
            return false;
        }

        ArmorItem newArmor = (ArmorItem) newItem.getItem();
        ArmorItem currentArmor = (ArmorItem) currentItem.getItem();

        // 比較防護值
        int currentArmorValue = currentArmor.getDefense();
        int newArmorValue = newArmor.getDefense();

        return newArmorValue > currentArmorValue; // 如果新裝備的防護值更高，則替換
    }

}

