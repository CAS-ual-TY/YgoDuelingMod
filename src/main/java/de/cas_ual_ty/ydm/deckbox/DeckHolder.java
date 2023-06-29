package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    
    // sleeves
    public static final int EXTRA_STUFF_SIZE = 1;
    public static final int SLEEVES_INDEX = SIDE_DECK_INDEX_END;
    
    public static final int TOTAL_SIZE_WITH_EXTRAS = DeckHolder.TOTAL_DECK_SIZE + EXTRA_STUFF_SIZE;
    
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
        return mainDeck;
    }
    
    public List<CardHolder> getExtraDeck()
    {
        return extraDeck;
    }
    
    public List<CardHolder> getSideDeck()
    {
        return sideDeck;
    }
    
    public CardSleevesType getSleeves()
    {
        return sleeves;
    }
    
    public boolean isEmpty()
    {
        return getMainDeckSize() == 0 && getExtraDeckSize() == 0 && getSideDeckSize() == 0;
    }
    
    public Stream<CardHolder> getMainDeckNonNull()
    {
        return getMainDeck().stream().filter((ch) -> ch != null);
    }
    
    public Stream<CardHolder> getExtraDeckNonNull()
    {
        return getExtraDeck().stream().filter((ch) -> ch != null);
    }
    
    public Stream<CardHolder> getSideDeckNonNull()
    {
        return getSideDeck().stream().filter((ch) -> ch != null);
    }
    
    public int getMainDeckSize()
    {
        return (int) getMainDeckNonNull().count();
    }
    
    public int getExtraDeckSize()
    {
        return (int) getExtraDeckNonNull().count();
    }
    
    public int getSideDeckSize()
    {
        return (int) getSideDeckNonNull().count();
    }
    
    @Override
    public String toString()
    {
        return mainDeck.toString() + extraDeck.toString() + sideDeck.toString();
    }
}
