package com.edcl.minecraftfriendmod.item;

import com.edcl.minecraftfriendmod.minecraftfriendmod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class friendscroll {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, minecraftfriendmod.MODID);

    public static final RegistryObject<Item> FRIENDSCROLL =
            ITEMS.register("friendscroll",
            ()-> new Item(
                    new Item.Properties()
            ));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
