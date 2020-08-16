package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.card.ItemStackCardHolder;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerDeckHolder extends DeckHolder
{
    public ItemHandlerDeckHolder(IItemHandler itemHandler)
    {
        super();
        
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
}
