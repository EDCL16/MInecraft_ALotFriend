package com.edcl.alotfriendmod.entity;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ALotFriendMod.MOD_ID);

    public static final RegistryObject<EntityType<FriendEntity>> FRIEND =
            ENTITY_TYPES.register("friend", () -> EntityType.Builder.of(FriendEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f).build("friend"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
