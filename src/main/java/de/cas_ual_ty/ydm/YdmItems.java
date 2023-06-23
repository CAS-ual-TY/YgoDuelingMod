package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.cardbinder.CardBinderItem;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.deckbox.PatreonDeckBoxItem;
import de.cas_ual_ty.ydm.duel.dueldisk.DuelDiskItem;
import de.cas_ual_ty.ydm.set.CardSetItem;
import de.cas_ual_ty.ydm.set.OpenedCardSetItem;
import de.cas_ual_ty.ydm.simplebinder.SimpleBinderItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.*;

public class YdmItems
{
    private static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, YDM.MOD_ID);
    
    public static final RegistryObject<Item> BLANC_CARD = null;
    public static final RegistryObject<Item> CARD_BACK = null;
    public static final RegistryObject<Item> BLANC_SET = null;
    public static final RegistryObject<CardItem> CARD = null;
    public static final RegistryObject<CardSetItem> SET = null;
    public static final RegistryObject<OpenedCardSetItem> OPENED_SET = null;
    public static final RegistryObject<CardBinderItem> CARD_BINDER = null;
    public static final RegistryObject<Item> DUEL_PLAYMAT = null;
    public static final RegistryObject<Item> DUEL_TABLE = null;
    public static final RegistryObject<Item> CARD_SUPPLY = null;
    
    public static final SimpleBinderItem SIMPLE_BINDER_3 = null;
    public static final SimpleBinderItem SIMPLE_BINDER_9 = null;
    public static final SimpleBinderItem SIMPLE_BINDER_27 = null;
    
    public static final Item DUEL_DISK = null;
    public static final Item CHAOS_DISK = null;
    public static final Item ACADEMIA_DISK = null;
    public static final Item ACADEMIA_DISK_RED = null;
    public static final Item ACADEMIA_DISK_BLUE = null;
    public static final Item ACADEMIA_DISK_YELLOW = null;
    public static final Item ROCK_SPIRIT_DISK = null;
    public static final Item CYBER_DESIGN_INTERFACE = null;
    
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
    public static final DeckBoxItem PATREON_DECK_BOX = null;
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register("blanc_card", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
        DEFERRED_REGISTER.register("card_back", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
        DEFERRED_REGISTER.register("blanc_set", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
        DEFERRED_REGISTER.register("card", () -> new CardItem(new Item.Properties().tab(YDM.cardsItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("set", () -> new CardSetItem(new Item.Properties().tab(YDM.setsItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("opened_set", () -> new OpenedCardSetItem(new Item.Properties().stacksTo(1)));
        DEFERRED_REGISTER.register("card_binder", () -> new CardBinderItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("duel_playmat", () -> new BlockItem(YdmBlocks.DUEL_PLAYMAT.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
        DEFERRED_REGISTER.register("duel_table", () -> new BlockItem(YdmBlocks.DUEL_TABLE.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
        DEFERRED_REGISTER.register("card_supply", () -> new BlockItem(YdmBlocks.CARD_SUPPLY.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
        
        DEFERRED_REGISTER.register("simple_binder_" + 3, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 3));
        DEFERRED_REGISTER.register("simple_binder_" + 9, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 9));
        DEFERRED_REGISTER.register("simple_binder_" + 27, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 27));
        
        DEFERRED_REGISTER.register("millennium_eye", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_key", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_necklace", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_puzzle", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_ring", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_rod", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("millennium_scale", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        
        DEFERRED_REGISTER.register("duel_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("chaos_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("academia_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("academia_disk_red", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("academia_disk_blue", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("academia_disk_yellow", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("rock_spirit_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("trueman_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("jewel_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("kaibaman_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("cyber_design_interface", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        
        DEFERRED_REGISTER.register("black_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("red_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("green_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("brown_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("blue_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("purple_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("cyan_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("light_gray_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("gray_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("pink_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("lime_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("yellow_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("light_blue_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("magenta_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("orange_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("white_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("iron_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("gold_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("diamond_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("emerald_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        DEFERRED_REGISTER.register("patreon_deck_box", () -> new PatreonDeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
        
        for(CardSleevesType sleeve : CardSleevesType.VALUES)
        {
            if(!sleeve.isCardBack())
            {
                DEFERRED_REGISTER.register(sleeve.getResourceName(), () -> new CardSleevesItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1), sleeve));
            }
        }
    }
}