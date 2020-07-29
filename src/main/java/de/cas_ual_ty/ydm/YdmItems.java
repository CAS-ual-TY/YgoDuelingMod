package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.binder.CardBinderItem;
import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.card.CosmeticItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmItems
{
    public static final Item BLANC_CARD = null;
    public static final Item CARD_BACK = null;
    public static final CardItem CARD = null;
    public static final CardBinderItem CARD_BINDER = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new CosmeticItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "blanc_card"));
        registry.register(new CosmeticItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "card_back"));
        registry.register(new CardItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "card"));
        registry.register(new CardBinderItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "card_binder"));
    }
}