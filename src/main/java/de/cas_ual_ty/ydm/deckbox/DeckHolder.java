package de.cas_ual_ty.ydm.deckbox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesType;

public class DeckHolder
{
    public static final int MAIN_DECK_SIZE = 60;
    public static final int EXTRA_DECK_SIZE = 15;
    public static final int SIDE_DECK_SIZE = 15;
    public static final int TOTAL_DECK_SIZE = DeckHolder.MAIN_DECK_SIZE + DeckHolder.EXTRA_DECK_SIZE + DeckHolder.SIDE_DECK_SIZE;
    // _end index is excluded
    public static final int MAIN_DECK_INDEX_START = 0;
    public static final int MAIN_DECK_INDEX_END = DeckHolder.MAIN_DECK_INDEX_START + DeckHolder.MAIN_DECK_SIZE;
    public static final int EXTRA_DECK_INDEX_START = DeckHolder.MAIN_DECK_INDEX_END;
    public static final int EXTRA_DECK_INDEX_END = DeckHolder.EXTRA_DECK_INDEX_START + DeckHolder.EXTRA_DECK_SIZE;
    public static final int SIDE_DECK_INDEX_START = DeckHolder.EXTRA_DECK_INDEX_END;
    public static final int SIDE_DECK_INDEX_END = DeckHolder.SIDE_DECK_INDEX_START + DeckHolder.SIDE_DECK_SIZE;
    
    public static final DeckHolder DUMMY = new DeckHolder();
    
    // these lists can contain null
    // this is to have deck boxes show gaps in them
    protected List<CardHolder> mainDeck;
    protected List<CardHolder> extraDeck;
    protected List<CardHolder> sideDeck;
    
    protected CardSleevesType sleeves;
    
    public DeckHolder(List<CardHolder> mainDeck, List<CardHolder> extraDeck, List<CardHolder> sideDeck, CardSleevesType sleeves)
    {
        this.mainDeck = mainDeck;
        this.extraDeck = extraDeck;
        this.sideDeck = sideDeck;
        this.sleeves = sleeves;
    }
    
    public DeckHolder(List<CardHolder> mainDeck, List<CardHolder> extraDeck, List<CardHolder> sideDeck)
    {
        this(mainDeck, extraDeck, sideDeck, CardSleevesType.CARD_BACK);
    }
    
    public DeckHolder()
    {
        this(new ArrayList<>(DeckHolder.MAIN_DECK_SIZE), new ArrayList<>(DeckHolder.EXTRA_DECK_SIZE), new ArrayList<>(DeckHolder.SIDE_DECK_SIZE), CardSleevesType.CARD_BACK);
    }
    
    public List<CardHolder> getMainDeck()
    {
        return this.mainDeck;
    }
    
    public List<CardHolder> getExtraDeck()
    {
        return this.extraDeck;
    }
    
    public List<CardHolder> getSideDeck()
    {
        return this.sideDeck;
    }
    
    public CardSleevesType getSleeves()
    {
        return this.sleeves;
    }
    
    public boolean isEmpty()
    {
        return this.getMainDeckSize() == 0 && this.getExtraDeckSize() == 0 && this.getSideDeckSize() == 0;
    }
    
    public Stream<CardHolder> getMainDeckNonNull()
    {
        return this.getMainDeck().stream().filter((ch) -> ch != null);
    }
    
    public Stream<CardHolder> getExtraDeckNonNull()
    {
        return this.getExtraDeck().stream().filter((ch) -> ch != null);
    }
    
    public Stream<CardHolder> getSideDeckNonNull()
    {
        return this.getSideDeck().stream().filter((ch) -> ch != null);
    }
    
    public int getMainDeckSize()
    {
        return (int)this.getMainDeckNonNull().count();
    }
    
    public int getExtraDeckSize()
    {
        return (int)this.getExtraDeckNonNull().count();
    }
    
    public int getSideDeckSize()
    {
        return (int)this.getSideDeckNonNull().count();
    }
    
    @Override
    public String toString()
    {
        return this.mainDeck.toString() + this.extraDeck.toString() + this.sideDeck.toString();
    }
}
