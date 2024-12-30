package com.edcl.alotfriendmod.item;

import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ALotFriendMod.MOD_ID);

    public static RegistryObject<Item> FRIEND_STONE =ITEMS.register("friendstone",
            () -> new ForgeSpawnEggItem(ModEntities.FRIEND, 0xffffff, 0xfffff, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
