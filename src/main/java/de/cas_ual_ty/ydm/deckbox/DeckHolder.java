package de.cas_ual_ty.ydm.deckbox;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public class DeckHolder implements IDeckHolder
{
    protected List<CardHolder> mainDeck;
    protected List<CardHolder> extraDeck;
    protected List<CardHolder> sideDeck;
    
    public DeckHolder()
    {
        this.mainDeck = new ArrayList<>(IDeckHolder.MAIN_DECK_SIZE);
        this.extraDeck = new ArrayList<>(IDeckHolder.EXTRA_DECK_SIZE);
        this.sideDeck = new ArrayList<>(IDeckHolder.SIDE_DECK_SIZE);
    }
    
    @Override
    public List<CardHolder> getMainDeck()
    {
        return this.mainDeck;
    }
    
    @Override
    public List<CardHolder> getExtraDeck()
    {
        return this.extraDeck;
    }
    
    @Override
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
