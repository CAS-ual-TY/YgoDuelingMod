package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.card.CosmeticItem;
import de.cas_ual_ty.ydm.cardbinder.CardBinderItem;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.dueldisk.DuelDiskItem;
import net.minecraft.item.BlockItem;
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
    public static final Item PLAYMAT = null;
    public static final Item DUEL_DISK = null;
    
    public static final DeckBoxItem BLACK_DECK_BOX = null;
    public static final DeckBoxItem RED_DECK_BOX = null;
    public static final DeckBoxItem GREEN_DECK_BOX = null;
    public static final DeckBoxItem BROWN_DECK_BOX = null;
    public static final DeckBoxItem BLUE_DECK_BOX = null;
    public static final DeckBoxItem PURPLE_DECK_BOX = null;
    public static final DeckBoxItem CYAN_DECK_BOX = null;
    public static final DeckBoxItem LIGHT_GRAY_DECK_BOX = null;
    public static final DeckBoxItem GRAY_DECK_BOX = null;
    public static final DeckBoxItem PINK_DECK_BOX = null;
    public static final DeckBoxItem LIME_DECK_BOX = null;
    public static final DeckBoxItem YELLOW_DECK_BOX = null;
    public static final DeckBoxItem LIGHT_BLUE_DECK_BOX = null;
    public static final DeckBoxItem MAGENTA_DECK_BOX = null;
    public static final DeckBoxItem ORANGE_DECK_BOX = null;
    public static final DeckBoxItem WHITE_DECK_BOX = null;
    public static final DeckBoxItem IRON_DECK_BOX = null;
    public static final DeckBoxItem GOLD_DECK_BOX = null;
    public static final DeckBoxItem DIAMOND_DECK_BOX = null;
    public static final DeckBoxItem EMERALD_DECK_BOX = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new CosmeticItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "blanc_card"));
        registry.register(new CosmeticItem(new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "card_back"));
        registry.register(new CardItem(new Properties().group(YDM.cardsItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "card"));
        registry.register(new CardBinderItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "card_binder"));
        registry.register(new BlockItem(YdmBlocks.PLAYMAT, new Properties().group(YDM.ydmItemGroup)).setRegistryName(YDM.MOD_ID, "playmat"));
        registry.register(new DuelDiskItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "duel_disk"));
        
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "black_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "red_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "green_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "brown_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "blue_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "purple_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "cyan_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "light_gray_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "gray_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "pink_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "lime_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "yellow_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "light_blue_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "magenta_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "orange_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "white_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "iron_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "gold_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "diamond_deck_box"));
        registry.register(new DeckBoxItem(new Properties().group(YDM.ydmItemGroup).maxStackSize(1)).setRegistryName(YDM.MOD_ID, "emerald_deck_box"));
    }
}