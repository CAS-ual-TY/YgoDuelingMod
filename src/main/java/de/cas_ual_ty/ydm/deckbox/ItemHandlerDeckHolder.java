package de.cas_ual_ty.ydm.deckbox;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.ItemStackCardHolder;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerDeckHolder implements IDeckHolder
{
    protected List<CardHolder> mainDeck;
    protected List<CardHolder> extraDeck;
    protected List<CardHolder> sideDeck;
    
    public ItemHandlerDeckHolder(IItemHandler itemHandler)
    {
        this.mainDeck = new ArrayList<>(IDeckHolder.MAIN_DECK_SIZE);
        this.extraDeck = new ArrayList<>(IDeckHolder.EXTRA_DECK_SIZE);
        this.sideDeck = new ArrayList<>(IDeckHolder.SIDE_DECK_SIZE);
        
        int index = 0;
        
        for(int i = 0; i < IDeckHolder.MAIN_DECK_SIZE; ++i)
        {
            this.mainDeck.add(new ItemStackCardHolder(itemHandler.getStackInSlot(index++)));
        }
        
        for(int i = 0; i < IDeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            this.extraDeck.add(new ItemStackCardHolder(itemHandler.getStackInSlot(index++)));
        }
        
        for(int i = 0; i < IDeckHolder.SIDE_DECK_SIZE; ++i)
        {
            this.sideDeck.add(new ItemStackCardHolder(itemHandler.getStackInSlot(index++)));
        }
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
}
