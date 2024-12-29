package com.edcl.alotfriendmod.event;


import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.ModEntities;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ALotFriendMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, FriendEntity::canSpawn);
        });

    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FRIEND.get(), FriendEntity.createAttributes().build());
    }
}