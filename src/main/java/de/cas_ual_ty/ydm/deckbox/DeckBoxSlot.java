package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DeckBoxSlot extends SlotItemHandler
{
    public DeckBoxSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }
    
    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return stack.getItem() == YdmItems.CARD;
    }
}
