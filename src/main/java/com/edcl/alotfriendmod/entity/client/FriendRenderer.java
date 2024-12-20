package com.edcl.alotfriendmod.entity.client;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class FriendRenderer extends MobRenderer<FriendEntity,FriendModel<FriendEntity>> {

    public FriendRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new FriendModel<>(pContext.bakeLayer(ModModelLayers.FRIEND_LAYER)), 2f);
    }

    @Override
    public ResourceLocation getTextureLocation(FriendEntity pEntity) {
        return new ResourceLocation(ALotFriendMod.MOD_ID, pEntity.getSelectedTexture());
    }

    @Override
    public void render(FriendEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {

        if(pEntity.isBaby()) {
            pPoseStack.scale(0.5f,0.5f,0.5f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    private static final String TEXTURE_FOLDER = "textures/entity/";
    private static final List<String> TEXTURES = List.of(
            "myth/ame","myth/calliope","myth/ina","myth/ina","myth/kiara",
            "female_1","female_2","female_3","female_4","female_5",
            "female_6","female_7","female_8","female_9","female_10",
            "female_11","female_12","female_13","female_14","female_15");

    public static String GetRandomTexture() {
        int randomIndex = (int) (Math.random() * TEXTURES.size());
        String ramdonTextureName = TEXTURES.get(randomIndex);
        return TEXTURE_FOLDER + ramdonTextureName +".png";
    }
}
