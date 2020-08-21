package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
    public ResourceLocation getShownIcon(PlayerEntity player)
    {
        ItemStack itemStack = player.inventory.getStackInSlot(this.slot);
        
        if(itemStack.getItem() instanceof DeckBoxItem)
        {
            return new ResourceLocation(YDM.MOD_ID, "textures/item/" + itemStack.getItem().getRegistryName().getPath() + ".png");
        }
        
        return super.getShownIcon(player);
    }
}
