package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.deckbox.DeckBuilder;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.deckbox.InventoryDeckProvider;
import de.cas_ual_ty.ydm.deckbox.SimpleDeckProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmDeckProviders
{
    public static final SimpleDeckProvider DUMMY = null;
    public static final SimpleDeckProvider DEBUG_DECK = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<DeckProvider> event)
    {
        IForgeRegistry<DeckProvider> registry = event.getRegistry();
        
        registry.register(new SimpleDeckProvider(() -> DeckHolder.DUMMY).setRegistryName(YDM.MOD_ID, "dummy"));
        
        registry.register(new SimpleDeckProvider(
            new DeckBuilder()
                
                .startMainDeck()
                .name("Ojama Green")
                .repeat()
                .repeat()
                .name("Ojama Yellow")
                .repeat()
                .repeat()
                .name("Ojama Black")
                .repeat()
                .repeat()
                .name("Ojama Blue")
                .repeat()
                .repeat()
                .name("Ojama Red")
                .name("Rescue Cat")
                .repeat()
                .name("Ojama Country")
                .repeat()
                .repeat()
                .name("Ojama Delta Hurricane!!")
                .repeat()
                .name("Ojamagic")
                .repeat()
                .repeat()
                .name("Terraforming")
                .name("Twin Twisters")
                .repeat()
                .name("Polymerization")
                .repeat()
                .repeat()
                .name("Magical Hats")
                .repeat()
                .repeat()
                .name("Ojama Duo")
                .repeat()
                .repeat()
                .name("Ojama Trio")
                .repeat()
                .repeat()
                .name("Drowning Mirror Force")
                .repeat()
                
                .startExtraDeck()
                .name("Ojama King")
                .repeat()
                .name("Ojama Knight")
                .name("Armored Kappa")
                .name("Herald of Pure Light")
                .name("Number 64: Ronin Raccoon Sandayu")
                .name("Number 96: Dark Mist")
                .name("Paleozoic Anomalocaris")
                .name("Paleozoic Opabinia")
                .name("Sky Cavalry Centaurea")
                .repeat()
                .name("The Phantom Knights of Cursed Javelin")
                .repeat()
                .name("Ojama Emperor")
                .repeat()
                
                .startSideDeck()
                .name("King of the Swamp")
                .repeat()
                .repeat()
                .name("Ojama Red")
                .name("Ojama Knight")
                .name("Ojama Emperor")
                .name("Future Fusion")
                .repeat()
                .repeat()
                .name("Twin Twisters")
                .name("Imperial Order")
                .name("Recall")
                .repeat()
                .name("Solemn Judgment")
                .name("Rescue Cat")
                
                .build(), () -> YdmDatabase.PROPERTIES_LIST.get(12482652L).getInfoImageResourceLocation((byte)0)).setRegistryName(YDM.MOD_ID, "debug_deck"));
        
        // player inventory incl main hand, excl armor
        for(int i = 0; i < 36; ++i)
        {
            registry.register(new InventoryDeckProvider(i).setRegistryName(YDM.MOD_ID, "inventory_slot_" + i));
        }
        
        // off hand
        registry.register(new DeckProvider()
        {
            
            @Override
            public DeckHolder provideDeck(PlayerEntity player)
            {
                ItemStack itemStack = player.getHeldItemOffhand();
                
                if(itemStack.getItem() instanceof DeckBoxItem)
                {
                    return YdmItems.BLACK_DECK_BOX.getDeckHolder(itemStack);
                }
                
                return null;
            }
        }.setRegistryName(YDM.MOD_ID, "inventory_offhand"));
    }
}