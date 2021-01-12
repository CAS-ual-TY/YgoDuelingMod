package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.carditeminventory.CardItemInventoryContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CardSetContainer extends CardItemInventoryContainer
{
    protected ItemStack itemStack;
    
    public CardSetContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler, ItemStack itemStack)
    {
        super(type, id, playerInventoryIn, itemHandler);
        this.itemStack = itemStack;
    }
    
    public CardSetContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn)
    {
        super(type, id, playerInventoryIn);
        this.itemStack = null;
    }
    
    @Override
    protected void createTopSlots()
    {
        for(int j = 0; j < 6; ++j)
        {
            for(int k = 0; k < 9; ++k)
            {
                this.addSlot(new Slot(this.slotInv, k + j * 9, 8 + k * 18, 18 + j * 18)
                {
                    @Override
                    public boolean isItemValid(ItemStack stack)
                    {
                        return CardSetContainer.this.canPutStack();
                    }
                });
            }
        }
    }
    
    @Override
    protected void createBottomSlots(PlayerInventory playerInventoryIn)
    {
        final int i = (6 - 4) * 18;
        
        for(int l = 0; l < 3; ++l)
        {
            for(int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i)
                {
                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn)
                    {
                        return this.getStack() != CardSetContainer.this.itemStack;
                    }
                });
            }
        }
        
        for(int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i)
            {
                @Override
                public boolean canTakeStack(PlayerEntity playerIn)
                {
                    return this.getStack() != CardSetContainer.this.itemStack;
                }
            });
        }
    }
    
    public boolean canPutStack()
    {
        return false;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        if(this.itemHandler != null && this.itemStack != null)
        {
            YdmItems.OPENED_SET.setItemHandler(this.itemStack, this.itemHandler);
        }
    }
}
