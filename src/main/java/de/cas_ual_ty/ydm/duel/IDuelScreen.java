package de.cas_ual_ty.ydm.duel;

import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;

public interface IDuelScreen
{
    public void reInit();
    
    public default void populateDeckSources(List<DeckSource> deckSources)
    {
    }
    
    public default void receiveDeck(int index, DeckHolder deck)
    {
    }
}
