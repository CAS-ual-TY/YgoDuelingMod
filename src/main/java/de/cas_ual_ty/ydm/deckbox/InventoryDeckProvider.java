package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryDeckProvider extends DeckProvider
{
    public final int slot;
    
    public InventoryDeckProvider(int slot)
    {
        this.slot = slot;
    }
    
    @Override
    public DeckHolder provideDeck(PlayerEntity player)
    {
        ItemStack itemStack = player.inventory.getStackInSlot(this.slot);
        
        if(itemStack.getItem() instanceof DeckBoxItem)
        {
            return YdmItems.BLACK_DECK_BOX.getDeckHolder(itemStack);
        }
        
        return null;
    }
    
    @Override
    public ItemStack getShownItem(PlayerEntity player)
    {
        ItemStack itemStack = player.inventory.getStackInSlot(this.slot);
        
        if(itemStack.getItem() instanceof DeckBoxItem)
        {
            return itemStack;
        }
        
        return super.getShownItem(player);
    }
}
