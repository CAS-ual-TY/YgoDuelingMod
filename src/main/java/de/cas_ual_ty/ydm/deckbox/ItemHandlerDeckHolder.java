package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.ItemStackCardHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerDeckHolder extends DeckHolder
{
    protected int mainDeckSize;
    protected int extraDeckSize;
    protected int sideDeckSize;
    
    public ItemHandlerDeckHolder(IItemHandler itemHandler)
    {
        super();
        
        mainDeckSize = 0;
        extraDeckSize = 0;
        sideDeckSize = 0;
        
        int index = 0;
        ItemStack itemStack;
        
        for(int i = 0; i < DeckHolder.MAIN_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                mainDeck.add(new ItemStackCardHolder(itemStack));
                ++mainDeckSize;
            }
            else
            {
                mainDeck.add(null);
            }
        }
        
        for(int i = 0; i < DeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                extraDeck.add(new ItemStackCardHolder(itemStack));
                ++extraDeckSize;
            }
            else
            {
                extraDeck.add(null);
            }
        }
        
        for(int i = 0; i < DeckHolder.SIDE_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                sideDeck.add(new ItemStackCardHolder(itemStack));
                ++sideDeckSize;
            }
            else
            {
                sideDeck.add(null);
            }
        }
        
        ItemStack sleevesStack = itemHandler.getStackInSlot(DeckHolder.SLEEVES_INDEX);
        
        if(sleevesStack.getItem() instanceof CardSleevesItem)
        {
            sleeves = ((CardSleevesItem) sleevesStack.getItem()).sleeves;
        }
    }
    
    @Override
    public int getMainDeckSize()
    {
        return mainDeckSize;
    }
    
    @Override
    public int getExtraDeckSize()
    {
        return extraDeckSize;
    }
    
    @Override
    public int getSideDeckSize()
    {
        return sideDeckSize;
    }
}
