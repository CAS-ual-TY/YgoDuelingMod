package de.cas_ual_ty.ydm.carditeminventory;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CIIContainer extends Container
{
    protected final PlayerEntity player;
    protected final Inventory slotInv;
    protected final IItemHandler itemHandler;
    
    protected int page;
    protected final int maxPage;
    protected boolean filling;
    
    public CIIContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler)
    {
        super(type, id);
        
        this.player = playerInventoryIn.player;
        this.slotInv = new Inventory(6 * 9);
        this.slotInv.addListener(this::onCraftMatrixChanged);
        this.itemHandler = itemHandler;
        
        this.createTopSlots();
        this.createBottomSlots(playerInventoryIn);
        
        this.page = 0;
        this.maxPage = 1 + this.itemHandler.getSlots() / (6 * 9);
        this.updateSlots();
    }
    
    public CIIContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, int itemHandlerSize)
    {
        this(type, id, playerInventoryIn, new ItemStackHandler(itemHandlerSize));
    }
    
    public CIIContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, PacketBuffer extraData)
    {
        this(type, id, playerInventoryIn, extraData.readInt());
    }
    
    protected void createTopSlots()
    {
        for(int j = 0; j < 6; ++j)
        {
            for(int k = 0; k < 9; ++k)
            {
                this.addSlot(new Slot(this.slotInv, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
    }
    
    protected void createBottomSlots(PlayerInventory playerInventoryIn)
    {
        final int i = (6 - 4) * 18;
        
        for(int l = 0; l < 3; ++l)
        {
            for(int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
        
        for(int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i));
        }
    }
    
    // sets the page but does not update anything
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public int getPage()
    {
        return this.page;
    }
    
    public int getMaxPage()
    {
        return this.maxPage;
    }
    
    protected void updatePage()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)this.player), new CIIMessages.SetPage(this.page));
    }
    
    public void nextPage()
    {
        ++this.page;
        
        if(this.page > this.maxPage)
        {
            this.page = 0;
        }
        
        this.updateSlots();
    }
    
    public void prevPage()
    {
        --this.page;
        
        if(this.page < 0)
        {
            this.page = this.maxPage;
        }
        
        this.updateSlots();
    }
    
    public void updateSlots()
    {
        if(this.itemHandler == null)
        {
            return;
        }
        
        this.filling = true;
        
        int start = this.page * this.slotInv.getSizeInventory();
        int end = start + this.slotInv.getSizeInventory();
        int i, j;
        
        for(i = start, j = 0; i < end && i < this.itemHandler.getSlots(); ++i, ++j)
        {
            this.slotInv.setInventorySlotContents(j, this.itemHandler.getStackInSlot(i));
        }
        
        for(; i < this.itemHandler.getSlots(); ++i)
        {
            this.slotInv.setInventorySlotContents(j, ItemStack.EMPTY);
        }
        
        this.filling = false;
        
        this.detectAndSendChanges();
    }
    
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if(this.itemHandler == null)
        {
            return;
        }
        
        if(!this.filling)
        {
            int start = this.page * this.slotInv.getSizeInventory();
            int end = start += this.slotInv.getSizeInventory();
            int i, j;
            
            for(i = start, j = 0; i < end && i < this.itemHandler.getSlots(); ++i, ++j)
            {
                this.itemHandler.insertItem(i, this.slotInv.getStackInSlot(j), false);
            }
            
            super.onCraftMatrixChanged(inventoryIn);
        }
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        
        Slot slot = this.inventorySlots.get(index);
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(index < 6 * 9)
            {
                if(!this.mergeItemStack(itemstack1, 6 * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(itemstack1, 0, 6 * 9, false))
            {
                return ItemStack.EMPTY;
            }
            
            if(itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }
        
        return itemstack;
    }
}