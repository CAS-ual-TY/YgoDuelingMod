package de.cas_ual_ty.ydm.duelmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
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
    
    public static final Supplier<DeckHolder> KING_SCRUBBY_DECK_1 = new DeckBuilder()
        
        .startMainDeck()
        .id(46986414L)
        .repeat()
        .repeat()
        .id(7084129L)
        .repeat()
        .id(30603688L)
        .repeat()
        .id(38033121L)
        .repeat()
        .id(35191415L)
        .repeat()
        .id(14824019L)
        .id(97268402L)
        .id(97631303L)
        .repeat()
        .id(47222536L)
        .repeat()
        .repeat()
        .id(75190122L)
        .id(60709218L)
        .id(41735184L)
        .id(2314238L)
        .id(89739383L)
        .id(23314220L)
        .repeat()
        .id(1784686L)
        .id(12580477L)
        .id(73915051L)
        .id(83764719L, (byte)1)
        .id(48680970L)
        .repeat()
        .repeat()
        .id(7922915L)
        .repeat()
        .repeat()
        .id(41420027L)
        .repeat()
        .id(84749824L)
        .id(40605147L)
        .repeat()
        
        .startExtraDeck()
        .id(37818794L)
        .id(41721210L)
        .id(50237654L)
        .id(83994433L)
        .id(44508094L)
        .id(76774528L)
        .id(96471335L)
        .repeat()
        .id(85551711L)
        .id(80117527L)
        .id(92918648L)
        
        .startSideDeck()
        .id(84433295L)
        .id(97631303L)
        .id(15693423L)
        .id(63391643L)
        .id(89739383L)
        .id(11827244L)
        .id(77565204L)
        .id(73616671L)
        
        .build();
    
    public static final Supplier<DeckHolder> BLESS_DECK_1 = new DeckBuilder()
        
        .startMainDeck()
        .name("Nibiru, the Primal Being")
        .name("Apprentice Illusion Magician")
        .repeat()
        .name("Danger! Mothman!")
        .name("Duza the Meteor Cubic Vessel")
        .repeat()
        .repeat()
        .name("Summoner Monk")
        .repeat()
        .repeat()
        .name("Ash Blossom & Joyous Spring")
        .repeat()
        .name("Karakuri Barrel mdl 96 \"Shinkuro\"")
        .repeat()
        .name("Mecha Phantom Beast Warbluran")
        .repeat()
        .name("Vijam the Cubic Seed")
        .name("Effect Veiler")
        .name("Magicians' Souls")
        .repeat()
        .repeat()
        .name("Foolish Burial Goods")
        .repeat()
        .repeat()
        .name("Cosmic Cyclone")
        .repeat()
        .name("Called by the Grave")
        .name("Crossout Designator")
        .repeat()
        .name("Cubic Dharma")
        .name("Cubic Karma")
        .repeat()
        .repeat()
        .name("Black Garden")
        .name("Infinite Impermanence")
        .repeat()
        .repeat()
        .name("Unification of the Cubic Lords")
        .repeat()
        .repeat()
        
        .startExtraDeck()
        .name("Ultimaya Tzolkin")
        .name("Crystal Wing Synchro Dragon")
        .repeat()
        .name("Karakuri Steel Shogun mdl 00X \"Bureido\"")
        .name("Garden Rose Maiden")
        .name("Phonon Pulse Dragon")
        .name("Martial Metal Marcher")
        .name("Borreload Dragon")
        .name("Accesscode Talker")
        .name("Knightmare Unicorn")
        .name("Mecha Phantom Beast Auroradon")
        .name("Knightmare Phoenix")
        .name("Knightmare Cerberus")
        .name("Crystron Halqifibrax")
        .name("Barricadeborg Blocker")
        
        .build();
    
    public static DeckSource getOjamaDeck()
    {
        return DeckSource.makeDeckSource(DeckSource.OJAMA_DECK_MAKER, (c) -> c.getProperties().getName().equals("Ojama Delta Hurricane!!"));
    }
    
    public static List<DeckSource> getAllPatreonDecks()
    {
        List<DeckSource> list = new LinkedList<>();
        
        list.add(DeckSource.makeDeckSource(DeckSource.KING_SCRUBBY_DECK_1, (c) -> true));
        list.add(DeckSource.makeDeckSource(DeckSource.BLESS_DECK_1, (c) -> c.getProperties().getName().equals("Duza the Meteor Cubic Vessel")));
        
        return list;
    }
    
    public static DeckSource makeDeckSource(Supplier<DeckHolder> deckHolder, Predicate<Card> flagShipCardChooser)
    {
        DeckHolder deck = deckHolder.get();
        
        Card card = YdmDatabase.CARDS_LIST.getByIndex(0);
        
        if(deck.getMainDeck() != null && deck.getMainDeck().size() > 0)
        {
            card = deck.getMainDeck().get(0).getCard();
        }
        
        for(Card c : YdmDatabase.CARDS_LIST)
        {
            if(flagShipCardChooser.test(c))
            {
                card = c;
                break;
            }
        }
        
        return new DeckSource(deck, YdmItems.CARD.createItemForCard(card));
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
