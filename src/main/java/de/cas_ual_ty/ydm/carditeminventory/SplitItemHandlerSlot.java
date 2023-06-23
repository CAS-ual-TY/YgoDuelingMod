package de.cas_ual_ty.ydm.carditeminventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class SplitItemHandlerSlot extends Slot
{
    private static Container emptyInventory = new SimpleContainer(0);
    
    private final IItemHandler itemHandler;
    private final int itemHandlerIndex;
    
    public SplitItemHandlerSlot(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition, int itemHandlerIndex)
    {
        super(emptyInventory, slotIndex, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.itemHandlerIndex = itemHandlerIndex;
    }
    
    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if(stack.isEmpty())
            return false;
        return itemHandler.isItemValid(itemHandlerIndex, stack);
    }
    
    @Override
    @Nonnull
    public ItemStack getItem()
    {
        return getItemHandler().getStackInSlot(itemHandlerIndex);
    }
    
    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void set(@Nonnull ItemStack stack)
    {
        ((IItemHandlerModifiable) getItemHandler()).setStackInSlot(itemHandlerIndex, stack);
        setChanged();
    }
    
    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn)
    {
    
    }
    
    @Override
    public int getMaxStackSize()
    {
        return itemHandler.getSlotLimit(itemHandlerIndex);
    }
    
    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        
        IItemHandler handler = getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(itemHandlerIndex);
        if(handler instanceof IItemHandlerModifiable)
        {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;
            
            handlerModifiable.setStackInSlot(itemHandlerIndex, ItemStack.EMPTY);
            
            ItemStack remainder = handlerModifiable.insertItem(itemHandlerIndex, maxAdd, true);
            
            handlerModifiable.setStackInSlot(itemHandlerIndex, currentStack);
            
            return maxInput - remainder.getCount();
        }
        else
        {
            ItemStack remainder = handler.insertItem(itemHandlerIndex, maxAdd, true);
            
            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }
    
    @Override
    public boolean mayPickup(Player playerIn)
    {
        return !getItemHandler().extractItem(itemHandlerIndex, 1, true).isEmpty();
    }
    
    @Override
    @Nonnull
    public ItemStack remove(int amount)
    {
        return getItemHandler().extractItem(itemHandlerIndex, amount, false);
    }
    
    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }
}
