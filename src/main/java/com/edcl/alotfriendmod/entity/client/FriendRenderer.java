package com.edcl.alotfriendmod.entity.client;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FriendRenderer extends HumanoidMobRenderer<FriendEntity, HumanoidModel<FriendEntity>> {

    public FriendRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new FriendModel<>(pContext.bakeLayer(ModModelLayers.FRIEND_LAYER)), 0.5f);
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
    }

    private static final String TEXTURE_FOLDER = "textures/entity/";
    private static final List<String> TEXTURES = List.of(
            "myth/ame", "myth/calliope", "myth/ina", "myth/kiara","myth/gura",
            "promise/fauna","promise/kronii","promise/baelz","promise/mumei","promise/irys",
            "hololive/kobo","hololive/coco","hololive/rushia","hololive/susei","hololive/miko","hololive/towa","hololive/kanata","hololive/marine",
            "hololive/botan","hololive/subaru","hololive/anyanya","hololive/korone","hololive/pekora","hololive/ayame",
            "female/1", "female/2", "female/3", "female/4", "female/5","female/6", "female/7", "female/8", "female/9", "female/10",
            "female/11", "female/12", "female/13", "female/14", "female/15","female/16", "female/17", "female/18","female/19","female/20",
            "special/1", "special/2", "special/3", "special/steve", "special/4","special/5","special/6","special/7","special/8","special/9","special/10","special/11");

    public static String GetRandomTexture() {
        int randomIndex = (int) (Math.random() * TEXTURES.size());
        String ramdonTextureName = TEXTURES.get(randomIndex);
        return TEXTURE_FOLDER + ramdonTextureName + ".png";
    }
}
