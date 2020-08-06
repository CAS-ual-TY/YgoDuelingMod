package de.cas_ual_ty.ydm.deckbox;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public interface IDeckHolder
{
    public static final int MAIN_DECK_SIZE = 60;
    public static final int EXTRA_DECK_SIZE = 15;
    public static final int SIDE_DECK_SIZE = 15;
    public static final int TOTAL_DECK_SIZE = IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE + IDeckHolder.SIDE_DECK_SIZE;
    
    // _end index is excluded
    public static final int MAIN_DECK_INDEX_START = 0;
    public static final int MAIN_DECK_INDEX_END = IDeckHolder.MAIN_DECK_INDEX_START + IDeckHolder.MAIN_DECK_SIZE;
    public static final int EXTRA_DECK_INDEX_START = IDeckHolder.MAIN_DECK_INDEX_END;
    public static final int EXTRA_DECK_INDEX_END = IDeckHolder.EXTRA_DECK_INDEX_START + IDeckHolder.EXTRA_DECK_SIZE;
    public static final int SIDE_DECK_INDEX_START = IDeckHolder.EXTRA_DECK_INDEX_END;
    public static final int SIDE_DECK_INDEX_END = IDeckHolder.SIDE_DECK_INDEX_START + IDeckHolder.SIDE_DECK_SIZE;
    
    public List<CardHolder> getMainDeck();
    
    public List<CardHolder> getExtraDeck();
    
    public List<CardHolder> getSideDeck();
    
    public static boolean isMainDeck(int slot)
    {
        return slot >= IDeckHolder.MAIN_DECK_INDEX_START && slot < IDeckHolder.MAIN_DECK_INDEX_END;
    }
    
    public static boolean isExtraDeck(int slot)
    {
        return slot >= IDeckHolder.EXTRA_DECK_INDEX_START && slot < IDeckHolder.EXTRA_DECK_INDEX_END;
    }
    
    public static boolean isSideDeck(int slot)
    {
        return slot >= IDeckHolder.SIDE_DECK_INDEX_START && slot < IDeckHolder.SIDE_DECK_INDEX_END;
    }
}
