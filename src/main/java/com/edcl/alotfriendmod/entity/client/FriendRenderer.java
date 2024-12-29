package com.edcl.alotfriendmod.entity.client;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;


import java.util.List;

public class FriendRenderer extends HumanoidMobRenderer<FriendEntity, HumanoidModel<FriendEntity>> {

    public FriendRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new FriendModel<>(pContext.bakeLayer(ModModelLayers.FRIEND_LAYER)), 0.5f);
        ModelLayerLocation pInnerModel =  ModelLayers.ARMOR_STAND_INNER_ARMOR;
        ModelLayerLocation pOuterModel =  ModelLayers.ARMOR_STAND_OUTER_ARMOR;
        //this.addLayer(new HumanoidArmorLayer<>(this, pInnerModel, pOuterModel, pContext.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(FriendEntity pEntity) {
        return new ResourceLocation(ALotFriendMod.MOD_ID, pEntity.getSelectedTexture());
    }

    @Override
    public void render(FriendEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        // 根據是否是小型生物縮放大小
        if (pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

        // 渲染頭盔
        renderItem(pPoseStack, pBuffer, pPackedLight, pEntity.getItemBySlot(EquipmentSlot.HEAD));
        // 渲染胸甲
        renderItem(pPoseStack, pBuffer, pPackedLight, pEntity.getItemBySlot(EquipmentSlot.CHEST));
        // 渲染護腿
        renderItem(pPoseStack, pBuffer, pPackedLight, pEntity.getItemBySlot(EquipmentSlot.LEGS));
        // 渲染鞋子
        renderItem(pPoseStack, pBuffer, pPackedLight, pEntity.getItemBySlot(EquipmentSlot.FEET));
    }

    // 渲染裝備
    public void renderItem(PoseStack poseStack, MultiBufferSource buffer, int light, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {


        }
    }

    private static final String TEXTURE_FOLDER = "textures/entity/";
    private static final List<String> TEXTURES = List.of(
            "myth/ame", "myth/calliope", "myth/ina", "myth/kiara",
            "female/1", "female/2", "female/3", "female/4", "female/5",
            "female/6", "female/7", "female/8", "female/9", "female/10",
            "female/11", "female/12", "female/13", "female/14", "female/15",
            "female/16", "female/17", "female/18",
            "special/1", "special/2", "special/3", "special/steve");

    public static String GetRandomTexture() {
        int randomIndex = (int) (Math.random() * TEXTURES.size());
        String ramdonTextureName = TEXTURES.get(randomIndex);
        return TEXTURE_FOLDER + ramdonTextureName + ".png";
    }
}
