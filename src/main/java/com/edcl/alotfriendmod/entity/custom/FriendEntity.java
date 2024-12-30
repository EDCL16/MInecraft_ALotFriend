package com.edcl.alotfriendmod.entity.custom;

import com.edcl.alotfriendmod.entity.client.FriendRenderer;
import com.edcl.alotfriendmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendEntity extends PathfinderMob implements InventoryCarrier, Npc, Merchant {

    private static final Logger log = LoggerFactory.getLogger(FriendEntity.class);
    private final SimpleContainer inventory = new SimpleContainer(100);
    public  static final float MOVEMENT_SPEED =0.5f;
    // 飽食度屬性
    private int foodLevel = 20; // 0 表示餓，20 表示飽滿
    private static final int MAX_FOOD_LEVEL = 20; // 最大飽食度
    private static final int MIN_FOOD_LEVEL_FOR_HEALTH = 5;
    private int foodLevelTick =0;
    private static final int dropFoodLevelTicks = 600;
    private int hungerTick = 0;


    public FriendEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.selectedTexture = FriendRenderer.GetRandomTexture();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1,new FloatGoal(this));
        this.goalSelector.addGoal(1,new EatFoodGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
        }));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Animal;
        }));

        this.goalSelector.addGoal(3,new CollectItemOnGround(this));

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5,new StrollThroughVillageGoal(this,20));
        this.goalSelector.addGoal(7, new FollowPlayerGoal(this));
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
    public void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);

        // 掉落背包中的所有物品
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                this.spawnAtLocation(itemStack);
                this.inventory.setItem(i, ItemStack.EMPTY); // 清空背包槽位
            }
        }

        // 掉落盔甲槽中的所有裝備
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equippedItem = this.getItemBySlot(slot);
            if (!equippedItem.isEmpty()) {
                this.spawnAtLocation(equippedItem);
                this.setItemSlot(slot, ItemStack.EMPTY); // 清空該裝備槽
            }
        }

        ItemStack friendStoneStack = new ItemStack(ModItems.FRIEND_STONE.get());
        this.spawnAtLocation(friendStoneStack);
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
        return this.inventory; // 返回實際的背包
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
        return FriendOffers.GetOffers();
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
        return true;
    }

    public void checkAndUpdateEquipment() {
        this.checkAndEquip(EquipmentSlot.HEAD);
        this.checkAndEquip(EquipmentSlot.CHEST);
        this.checkAndEquip(EquipmentSlot.LEGS);
        this.checkAndEquip(EquipmentSlot.FEET);
    }

    private void checkAndEquip(EquipmentSlot slot) {
        ItemStack currentItem = this.getItemBySlot(slot);
        ItemStack bestItem = getBestItemForSlot(slot);

        if (isBetterEquipment(bestItem, currentItem)) {
            this.setItemSlot(slot, bestItem);
        }
    }

    private ItemStack getBestItemForSlot(EquipmentSlot slot) {
        ItemStack bestItem = ItemStack.EMPTY;
        int bestArmorValue = -1;

        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);

            if (itemStack.getItem() instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem) itemStack.getItem();

                EquipmentSlot itemSlot = getArmorSlotForItem(armorItem);

                if (itemSlot == slot) {
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

    private boolean isBetterEquipment(ItemStack newItem, ItemStack currentItem) {    // 比較裝備是否更好
        if (currentItem.isEmpty()) {
            return true;
        }

        // 確保裝備是 ArmorItem
        if (!(newItem.getItem() instanceof ArmorItem) || !(currentItem.getItem() instanceof ArmorItem)) {
            return false;
        }

        ArmorItem newArmor = (ArmorItem) newItem.getItem();
        ArmorItem currentArmor = (ArmorItem) currentItem.getItem();

        int currentArmorValue = currentArmor.getDefense();        // 比較防護值
        int newArmorValue = newArmor.getDefense();

        return newArmorValue > currentArmorValue; // 如果新裝備的防護值更高，則替換
    }

    public void pickUp(ItemEntity item) {
        if (item != null && !item.isRemoved()) { // 確保物品存在且未被移除
            ItemStack itemStack = item.getItem(); // 獲取物品堆
            int remainingItems = addToInventory(itemStack); // 將物品添加到背包

            if (remainingItems == 0) {
                item.discard(); // 如果全部拾取成功，移除地上的物品
            } else {
                item.getItem().setCount(remainingItems); // 否則更新剩餘數量
            }
        }
        checkAndUpdateEquipment();
        findBestWeaponAndEquip();
    }

    private int addToInventory(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack slotStack = this.inventory.getItem(i);

            // 如果槽位為空，直接放入物品
            if (slotStack.isEmpty()) {
                this.inventory.setItem(i, itemStack.copy());
                return 0;
            }

            // 如果槽位有相同物品，嘗試合併
            if (ItemStack.isSameItemSameTags(slotStack, itemStack)) {
                int maxStackSize = slotStack.getMaxStackSize();
                int space = maxStackSize - slotStack.getCount();

                if (space > 0) {
                    int countToAdd = Math.min(space, itemStack.getCount());
                    slotStack.grow(countToAdd);
                    itemStack.shrink(countToAdd);

                    if (itemStack.isEmpty()) {
                        return 0; // 全部物品已合併，返回成功
                    }
                }
            }
        }
        return itemStack.getCount(); // 返回未能存放的物品數量
    }

    public void eatFood(ItemStack foodItem) {
        // 判斷是否為食物
        if (foodItem.isEdible()) {
            int foodValue = foodItem.getItem().getFoodProperties().getNutrition();
            // 增加飽食度，不能超過最大值
            this.foodLevel = Math.min(this.foodLevel + foodValue, MAX_FOOD_LEVEL);
            // 食物使用後減少數量
            foodItem.shrink(1); // 使用一個食物
            this.showEatingAnimation();
        }
    }

    // 顯示吃東西的動畫或效果
    private void showEatingAnimation() {
        // 這裡可以添加顯示吃東西的動畫或效果
    }

    // 檢查該實體是否還需要進食
    public boolean isHungry() {
        return this.foodLevel < MAX_FOOD_LEVEL;
    }

    // 獲取飽食度
    public int getFoodLevel() {
        return this.foodLevel;
    }

    // 使飽食度逐漸減少
    public void reduceHunger() {
        if (this.foodLevel > 0) {
            if(dropFoodLevelTicks < foodLevelTick)
            {
                this.foodLevel--;
                foodLevelTick =0;
            }
            foodLevelTick++;
        }
    }

    // 減少飽食度的方法可加入到實體的生活週期中
    @Override
    public void aiStep() {
        super.aiStep();
        // 每步驟減少少量飽食度
        reduceHunger();
        updateHunger();
    }


    public void onFoodContact(ItemStack foodItem) {
        if (foodItem.isEdible()) {
            eatFood(foodItem);
        }
    }
    public void updateHunger() {
        if (this.foodLevel < MIN_FOOD_LEVEL_FOR_HEALTH) {
            hungerTick++;
            // 每隔50 ticks掉一次血
            if (hungerTick >= 50) {
                DamageType HUNGER_DAMAGE = new DamageType(
                        "hunger",
                        DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                        0.0F       );
                Holder<DamageType> hungerDamageTypeHolder = Holder.direct(HUNGER_DAMAGE);
                DamageSource hungerDamage = new DamageSource(hungerDamageTypeHolder);
                this.hurt(hungerDamage, 0.5F); // 受到飢餓傷害，這裡是每隔一段時間掉1滴血
                hungerTick = 0; // 重置計時器
            }
        }
    }

    public EntityGetter getLevel() {
        return level();
    }

    public void findBestWeaponAndEquip() {
        ItemStack bestWeapon = getBestWeaponInInventory();
        ItemStack currentWeapon = this.getMainHandItem();

        // 比較並決定是否裝備背包中最好的武器
        if (isBetterWeapon(bestWeapon, currentWeapon)) {
            // 裝備背包中最好的武器到主手
            this.setItemSlot(EquipmentSlot.MAINHAND, bestWeapon);
        }
    }
    private ItemStack getBestWeaponInInventory() {
        ItemStack bestWeapon = ItemStack.EMPTY;
        int bestMaterialPriority = -1;
        int bestWeaponTypePriority = -1;

        // 遍歷背包中的所有物品
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);

            // 判斷是否為武器（劍或斧頭）
            if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof AxeItem) {
                int materialPriority = getMaterialPriority(itemStack);
                int weaponTypePriority = getWeaponTypePriority(itemStack);

                // 比較材質優先級
                if (materialPriority > bestMaterialPriority ||
                        (materialPriority == bestMaterialPriority && weaponTypePriority > bestWeaponTypePriority)) {
                    bestMaterialPriority = materialPriority;
                    bestWeaponTypePriority = weaponTypePriority;
                    bestWeapon = itemStack;
                }
            }
        }

        return bestWeapon;
    }

    private int getMaterialPriority(ItemStack weapon) {
        // 根據材質返回優先級（獄髓 > 鑽石 > 鐵 > 黃金 > 石頭 > 木頭）
        if (weapon.getItem().toString().contains("netherite")) {
            return 6; // 獄髓
        } else if (weapon.getItem().toString().contains("diamond")) {
            return 5; // 鑽石
        } else if (weapon.getItem().toString().contains("iron")) {
            return 4; // 鐵
        } else if (weapon.getItem().toString().contains("gold")) {
            return 3; // 黃金
        } else if (weapon.getItem().toString().contains("stone")) {
            return 2; // 石頭
        } else if (weapon.getItem().toString().contains("wood")) {
            return 1; // 木頭
        }

        return 0; // 若材質無法辨識則返回最低優先級
    }

    private int getWeaponTypePriority(ItemStack weapon) {
        // 劍 > 斧頭
        if (weapon.getItem() instanceof SwordItem) {
            return 2; // 劍優先
        } else if (weapon.getItem() instanceof AxeItem) {
            return 1; // 斧頭次之
        }
        return 0; // 其他武器類型
    }

    private boolean isBetterWeapon(ItemStack newWeapon, ItemStack currentWeapon) {
        // 如果沒有當前武器或新武器材質更好或材質相同但武器類型更好
        return currentWeapon.isEmpty() ||
                getMaterialPriority(newWeapon) > getMaterialPriority(currentWeapon) ||
                (getMaterialPriority(newWeapon) == getMaterialPriority(currentWeapon) &&
                        getWeaponTypePriority(newWeapon) > getWeaponTypePriority(currentWeapon));
    }
}

