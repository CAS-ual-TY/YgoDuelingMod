package de.cas_ual_ty.ydm.deckbox;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

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
    
    public static final DeckHolder DUMMY_DECK = new DeckHolder();
    
    protected List<CardHolder> mainDeck;
    protected List<CardHolder> extraDeck;
    protected List<CardHolder> sideDeck;
    
    public DeckHolder(List<CardHolder> mainDeck, List<CardHolder> extraDeck, List<CardHolder> sideDeck)
    {
        this.mainDeck = mainDeck;
        this.extraDeck = extraDeck;
        this.sideDeck = sideDeck;
    }
    
    public DeckHolder()
    {
        this(new ArrayList<>(DeckHolder.MAIN_DECK_SIZE), new ArrayList<>(DeckHolder.EXTRA_DECK_SIZE), new ArrayList<>(DeckHolder.SIDE_DECK_SIZE));
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
    
    @Override
    public String toString()
    {
        return this.mainDeck.toString() + this.extraDeck.toString() + this.sideDeck.toString();
    }
}
