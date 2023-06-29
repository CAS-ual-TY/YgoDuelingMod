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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class YdmItems
{
    private static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, YDM.MOD_ID);
    
    public static final RegistryObject<Item> BLANC_CARD = DEFERRED_REGISTER.register("blanc_card", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
    public static final RegistryObject<Item> CARD_BACK = DEFERRED_REGISTER.register("card_back", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
    public static final RegistryObject<Item> BLANC_SET = DEFERRED_REGISTER.register("blanc_set", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup)));
    public static final RegistryObject<CardItem> CARD = DEFERRED_REGISTER.register("card", () -> new CardItem(new Item.Properties().tab(YDM.cardsItemGroup).stacksTo(1)));
    public static final RegistryObject<CardSetItem> SET = DEFERRED_REGISTER.register("set", () -> new CardSetItem(new Item.Properties().tab(YDM.setsItemGroup).stacksTo(1)));
    public static final RegistryObject<OpenedCardSetItem> OPENED_SET = DEFERRED_REGISTER.register("opened_set", () -> new OpenedCardSetItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CardBinderItem> CARD_BINDER = DEFERRED_REGISTER.register("card_binder", () -> new CardBinderItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> DUEL_PLAYMAT = DEFERRED_REGISTER.register("duel_playmat", () -> new BlockItem(YdmBlocks.DUEL_PLAYMAT.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
    public static final RegistryObject<Item> DUEL_TABLE = DEFERRED_REGISTER.register("duel_table", () -> new BlockItem(YdmBlocks.DUEL_TABLE.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
    public static final RegistryObject<Item> CARD_SUPPLY = DEFERRED_REGISTER.register("card_supply", () -> new BlockItem(YdmBlocks.CARD_SUPPLY.get(), new Item.Properties().tab(YDM.ydmItemGroup)));
    
    public static final RegistryObject<SimpleBinderItem> SIMPLE_BINDER_3 = DEFERRED_REGISTER.register("simple_binder_" + 3, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 3));
    public static final RegistryObject<SimpleBinderItem> SIMPLE_BINDER_9 = DEFERRED_REGISTER.register("simple_binder_" + 9, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 9));
    public static final RegistryObject<SimpleBinderItem> SIMPLE_BINDER_27 = DEFERRED_REGISTER.register("simple_binder_" + 27, () -> SimpleBinderItem.makeItem(YDM.MOD_ID, YDM.ydmItemGroup, 27));
    
    public static final RegistryObject<Item> MILLENIUM_EYE = DEFERRED_REGISTER.register("millennium_eye", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_KEY = DEFERRED_REGISTER.register("millennium_key", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_NECKLACE = DEFERRED_REGISTER.register("millennium_necklace", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_PUZZLE = DEFERRED_REGISTER.register("millennium_puzzle", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_RING = DEFERRED_REGISTER.register("millennium_ring", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_ROD = DEFERRED_REGISTER.register("millennium_rod", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> MILLENIUM_SCALE = DEFERRED_REGISTER.register("millennium_scale", () -> new CosmeticItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    
    public static final RegistryObject<Item> DUEL_DISK = DEFERRED_REGISTER.register("duel_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> CHAOS_DISK = DEFERRED_REGISTER.register("chaos_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> ACADEMIA_DISK = DEFERRED_REGISTER.register("academia_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> ACADEMIA_DISK_RED = DEFERRED_REGISTER.register("academia_disk_red", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> ACADEMIA_DISK_BLUE = DEFERRED_REGISTER.register("academia_disk_blue", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> ACADEMIA_DISK_YELLOW = DEFERRED_REGISTER.register("academia_disk_yellow", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> ROCK_SPIRIT_DISK = DEFERRED_REGISTER.register("rock_spirit_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> TRUEMAN_DISK = DEFERRED_REGISTER.register("trueman_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> JEWEL_DISK = DEFERRED_REGISTER.register("jewel_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> KAIBAMAN_DISK = DEFERRED_REGISTER.register("kaibaman_disk", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<Item> CYBER_DESIGN_INTERFACE = DEFERRED_REGISTER.register("cyber_design_interface", () -> new DuelDiskItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    
    public static final RegistryObject<DeckBoxItem> BLACK_DECK_BOX = DEFERRED_REGISTER.register("black_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> RED_DECK_BOX = DEFERRED_REGISTER.register("red_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> GREEN_DECK_BOX = DEFERRED_REGISTER.register("green_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> BROWN_DECK_BOX = DEFERRED_REGISTER.register("brown_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> BLUE_DECK_BOX = DEFERRED_REGISTER.register("blue_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> PURPLE_DECK_BOX = DEFERRED_REGISTER.register("purple_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> CYAN_DECK_BOX = DEFERRED_REGISTER.register("cyan_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> LIGHT_GRAY_DECK_BOX = DEFERRED_REGISTER.register("light_gray_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> GRAY_DECK_BOX = DEFERRED_REGISTER.register("gray_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> PINK_DECK_BOX = DEFERRED_REGISTER.register("pink_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> LIME_DECK_BOX = DEFERRED_REGISTER.register("lime_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> YELLOW_DECK_BOX = DEFERRED_REGISTER.register("yellow_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> LIGHT_BLUE_DECK_BOX = DEFERRED_REGISTER.register("light_blue_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> MAGENTA_DECK_BOX = DEFERRED_REGISTER.register("magenta_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> ORANGE_DECK_BOX = DEFERRED_REGISTER.register("orange_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> WHITE_DECK_BOX = DEFERRED_REGISTER.register("white_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> IRON_DECK_BOX = DEFERRED_REGISTER.register("iron_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> GOLD_DECK_BOX = DEFERRED_REGISTER.register("gold_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> DIAMOND_DECK_BOX = DEFERRED_REGISTER.register("diamond_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> EMERALD_DECK_BOX = DEFERRED_REGISTER.register("emerald_deck_box", () -> new DeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    public static final RegistryObject<DeckBoxItem> PATREON_DECK_BOX = DEFERRED_REGISTER.register("patreon_deck_box", () -> new PatreonDeckBoxItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1)));
    
    static
    {
        for(CardSleevesType sleeve : CardSleevesType.VALUES)
        {
            if(!sleeve.isCardBack())
            {
                DEFERRED_REGISTER.register(sleeve.getResourceName(), () -> new CardSleevesItem(new Item.Properties().tab(YDM.ydmItemGroup).stacksTo(1), sleeve));
            }
        }
    }
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}