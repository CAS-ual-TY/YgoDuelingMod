package de.cas_ual_ty.ydm.deckbox;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public interface IDeckHolder
{
    public static final int MAIN_DECK_SIZE = 60;
    public static final int EXTRA_DECK_SIZE = 15;
    public static final int SIDE_DECK_SIZE = 15;
    public static final int TOTAL_DECK_SIZE = IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE + IDeckHolder.SIDE_DECK_SIZE;
    
    public List<CardHolder> getMainDeck();
    
    public List<CardHolder> getExtraDeck();
    
    public List<CardHolder> getSideDeck();
    
    public static boolean isMainDeck(int slot)
    {
        return slot >= 0 && slot < IDeckHolder.MAIN_DECK_SIZE;
    }
    
    public static boolean isExtraDeck(int slot)
    {
        return slot >= IDeckHolder.MAIN_DECK_SIZE && slot < IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE;
    }
    
    public static boolean isSideDeck(int slot)
    {
        return slot >= IDeckHolder.MAIN_DECK_SIZE + IDeckHolder.EXTRA_DECK_SIZE && slot < IDeckHolder.TOTAL_DECK_SIZE;
    }
}
