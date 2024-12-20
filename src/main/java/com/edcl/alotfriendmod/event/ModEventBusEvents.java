package net.kaupenjoe.tutorialmod.event;


import com.edcl.alotfriendmod.ALotFriendMod;
import com.edcl.alotfriendmod.entity.ModEntities;
import com.edcl.alotfriendmod.entity.custom.FriendEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ALotFriendMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FRIEND.get(), FriendEntity.createAttributes().build());
    }
}