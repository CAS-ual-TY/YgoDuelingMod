package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DeckBoxContainer extends Container
{
    public ItemStack itemStack;
    public IItemHandler itemHandler;
    
    public DeckBoxContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, DeckBoxItem.getActiveDeckBox(playerInventory.player));
    }
    
    public DeckBoxContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, ItemStack itemStack)
    {
        super(type, id);
        
        this.itemStack = itemStack;
        
        this.itemHandler = YdmItems.BLACK_DECK_BOX.getItemHandler(this.itemStack);
        
        final int itemsPerRow = 15;
        
        // main deck
        for(int y = 0; y < DeckHolder.MAIN_DECK_SIZE / itemsPerRow; ++y)
        {
            for(int x = 0; x < itemsPerRow && x + y * itemsPerRow < DeckHolder.MAIN_DECK_SIZE; ++x)
            {
                this.addSlot(new SlotItemHandler(this.itemHandler, x + y * itemsPerRow + DeckHolder.MAIN_DECK_INDEX_START, 8 + x * 18, 18 + y * 18));
            }
        }
        
        // extra deck
        for(int x = 0; x < DeckHolder.EXTRA_DECK_SIZE; ++x)
        {
            this.addSlot(new SlotItemHandler(this.itemHandler, x + DeckHolder.EXTRA_DECK_INDEX_START, 8 + x * 18, 104));
        }
        
        // side deck
        for(int x = 0; x < DeckHolder.SIDE_DECK_SIZE; ++x)
        {
            this.addSlot(new SlotItemHandler(this.itemHandler, x + DeckHolder.SIDE_DECK_INDEX_START, 8 + x * 18, 136));
        }
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 62 + x * 18, 168 + y * 18));
            }
        }
        
        // player hot bar
        Slot s;
        for(int x = 0; x < 9; ++x)
        {
            s = new Slot(playerInventory, x, 62 + x * 18, 226);
            
            if(s.getStack() == this.itemStack)
            {
                s = new Slot(playerInventory, s.getSlotIndex(), s.xPos, s.yPos)
                {
                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn)
                    {
                        return false;
                    }
                };
            }
            
            this.addSlot(s);
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        if(index >= 0 && index < DeckHolder.TOTAL_DECK_SIZE)
        {
            // TODO
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        // TODO can be removed when capabilities work again
        ((DeckBoxItem)this.itemStack.getItem()).saveItemHandlerToNBT(this.itemStack, this.itemHandler);
        super.onContainerClosed(playerIn);
    }
}
