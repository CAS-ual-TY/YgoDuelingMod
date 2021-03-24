package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.card.ItemStackCardHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerDeckHolder extends DeckHolder
{
    protected int mainDeckSize;
    protected int extraDeckSize;
    protected int sideDeckSize;
    
    public ItemHandlerDeckHolder(IItemHandler itemHandler, ItemStack sleevesStack)
    {
        super();
        
        this.mainDeckSize = 0;
        this.extraDeckSize = 0;
        this.sideDeckSize = 0;
        
        int index = 0;
        ItemStack itemStack;
        
        for(int i = 0; i < DeckHolder.MAIN_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                this.mainDeck.add(new ItemStackCardHolder(itemStack));
                ++this.mainDeckSize;
            }
            else
            {
                this.mainDeck.add(null);
            }
        }
        
        for(int i = 0; i < DeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                this.extraDeck.add(new ItemStackCardHolder(itemStack));
                ++this.extraDeckSize;
            }
            else
            {
                this.extraDeck.add(null);
            }
        }
        
        for(int i = 0; i < DeckHolder.SIDE_DECK_SIZE; ++i)
        {
            itemStack = itemHandler.getStackInSlot(index++);
            
            if(!itemStack.isEmpty())
            {
                this.sideDeck.add(new ItemStackCardHolder(itemStack));
                ++this.sideDeckSize;
            }
            else
            {
                this.sideDeck.add(null);
            }
        }
        
        if(sleevesStack.getItem() instanceof CardSleevesItem)
        {
            this.sleeves = ((CardSleevesItem)sleevesStack.getItem()).sleeves;
        }
    }
    
    @Override
    public int getMainDeckSize()
    {
        return this.mainDeckSize;
    }
    
    @Override
    public int getExtraDeckSize()
    {
        return this.extraDeckSize;
    }
    
    @Override
    public int getSideDeckSize()
    {
        return this.sideDeckSize;
    }
}
