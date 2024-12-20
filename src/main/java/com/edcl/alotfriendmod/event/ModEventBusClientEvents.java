package com.edcl.alotfriendmod.event;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.client.FriendModel;
import com.edcl.alotfriendmod.entity.client.ModModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ALotFriendMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.FRIEND_LAYER, FriendModel::createBodyLayer);
    }
}
