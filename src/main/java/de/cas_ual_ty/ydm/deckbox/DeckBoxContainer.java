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
    public DeckBoxContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, DeckBoxItem.getActiveDeckBox(playerInventory.player));
    }
    
    public DeckBoxContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, ItemStack itemStack)
    {
        super(type, id);
        
        IItemHandler itemHandler = YdmItems.BLACK_DECK_BOX.getItemHandler(itemStack);
        
        final int itemsPerRow = 15;
        
        // main deck
        for(int y = 0; y < IDeckHolder.MAIN_DECK_SIZE / itemsPerRow; ++y)
        {
            for(int x = 0; x < itemsPerRow && x + y * itemsPerRow < IDeckHolder.MAIN_DECK_SIZE; ++x)
            {
                this.addSlot(new SlotItemHandler(itemHandler, x + y * itemsPerRow + IDeckHolder.MAIN_DECK_INDEX_START, 8 + x * 18, 18 + y * 18));
            }
        }
        
        // extra deck
        for(int x = 0; x < IDeckHolder.EXTRA_DECK_SIZE; ++x)
        {
            this.addSlot(new SlotItemHandler(itemHandler, x + IDeckHolder.EXTRA_DECK_INDEX_START, 8 + x * 18, 104));
        }
        
        // side deck
        for(int x = 0; x < IDeckHolder.SIDE_DECK_SIZE; ++x)
        {
            this.addSlot(new SlotItemHandler(itemHandler, x + IDeckHolder.SIDE_DECK_INDEX_START, 8 + x * 18, 136));
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
            
            if(s.getStack() == itemStack)
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
        if(index >= 0 && index < IDeckHolder.TOTAL_DECK_SIZE)
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
}
