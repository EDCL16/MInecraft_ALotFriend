package com.edcl.alotfriendmod.entity.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class FriendOffers {

    public static MerchantOffers GetOffers()
    {
        MerchantOffers offers = new MerchantOffers();

        ItemStack itemToBuy = new ItemStack(Items.DIAMOND, 1);
        ItemStack itemToSell = new ItemStack(Items.IRON_SWORD, 1);
        MerchantOffer offer = new MerchantOffer(itemToBuy, itemToSell, 1, 1, 0.05F);
        offers.add(offer);
        return offers;
    }
}
