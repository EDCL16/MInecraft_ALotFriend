package com.edcl.minecraftfriendmod.item;

import com.edcl.minecraftfriendmod.minecraftfriendmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class friendStone {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, minecraftfriendmod.MODID);

    public static final RegistryObject<Item> FRIENDSCROLL =
            ITEMS.register("friendstone",
            ()-> new Item(
                    new Item.Properties().useItemDescriptionPrefix().setId(
                            ResourceKey.create(
                                    Registries.ITEM, ResourceLocation.parse("minecraftfriendmod:friendstone")
                            )
                    )
            ));

//ITEMS.register("egg", () -> new Item(new Item.Properties().useItemDescriptionPrefix().
// setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse("modid:egg")))));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}