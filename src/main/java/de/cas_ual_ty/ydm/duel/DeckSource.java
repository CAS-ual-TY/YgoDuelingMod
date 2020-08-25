package de.cas_ual_ty.ydm.duel;

import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.deckbox.DeckBuilder;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import net.minecraft.item.ItemStack;

public class DeckSource
{
    /// --- OJAMA TESTING DECK START ---
    
    public static final Supplier<DeckHolder> OJAMA_DECK_MAKER = new DeckBuilder()
        
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
        
        .build();
    
    /// --- OJAMA TESTING DECK END ---
    
    public static DeckSource getOjamaDeck()
    {
        Card card = YdmDatabase.CARDS_LIST.getByIndex(0);
        
        for(Card c : YdmDatabase.CARDS_LIST)
        {
            if(c.getProperties().getName().equals("Ojama Delta Hurricane!!"))
            {
                card = c;
                break;
            }
        }
        
        return new DeckSource(DeckSource.OJAMA_DECK_MAKER.get(), YdmItems.CARD.createItemForCard(card));
    }
    
    public DeckHolder deck;
    public ItemStack source;
    
    public DeckSource(DeckHolder deck, ItemStack source)
    {
        this.deck = deck;
        this.source = source;
    }
    
    public DeckSource(DeckHolder deck)
    {
        this(deck, new ItemStack(YdmItems.CARD_BACK));
    }
}
