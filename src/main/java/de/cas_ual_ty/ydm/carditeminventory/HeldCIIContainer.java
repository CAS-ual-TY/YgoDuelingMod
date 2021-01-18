package de.cas_ual_ty.ydm.carditeminventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class HeldCIIContainer extends CIIContainer
{
    protected final Hand hand;
    
    public HeldCIIContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler, Hand hand)
    {
        super(type, id, playerInventoryIn, itemHandler);
        this.hand = hand;
    }
    
    public HeldCIIContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, PacketBuffer extraData)
    {
        this(type, id, playerInventoryIn, new ItemStackHandler(extraData.readInt()), extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND);
    }
    
    public abstract void saveItemHandler(PlayerEntity player, ItemStack itemStack, IItemHandler itemHandler);
    
    @Override
    protected void createBottomSlots(PlayerInventory playerInventoryIn)
    {
        if(this.hand == Hand.OFF_HAND)
        {
            super.createBottomSlots(playerInventoryIn);
            return;
        }
        
        final int i = (6 - 4) * 18;
        
        int id;
        
        for(int l = 0; l < 3; ++l)
        {
            for(int j1 = 0; j1 < 9; ++j1)
            {
                id = j1 + l * 9 + 9;
                
                if(id == playerInventoryIn.currentItem)
                {
                    this.addSlot(new Slot(playerInventoryIn, id, 8 + j1 * 18, 103 + l * 18 + i)
                    {
                        @Override
                        public boolean canTakeStack(PlayerEntity playerIn)
                        {
                            return false;
                        }
                    });
                }
                else
                {
                    this.addSlot(new Slot(playerInventoryIn, id, 8 + j1 * 18, 103 + l * 18 + i));
                }
            }
        }
        
        for(int i1 = 0; i1 < 9; ++i1)
        {
            id = i1;
            
            if(id == playerInventoryIn.currentItem)
            {
                this.addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i)
                {
                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn)
                    {
                        return false;
                    }
                });
            }
            else
            {
                this.addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i));
            }
        }
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        
        if(!playerIn.world.isRemote)
        {
            this.saveItemHandler(this.player, playerIn.getHeldItem(this.hand), this.itemHandler);
        }
    }
    
    public static void openGui(PlayerEntity player, Hand hand, int itemHandlerSize, INamedContainerProvider p)
    {
        NetworkHooks.openGui((ServerPlayerEntity)player, p, (extraData) ->
        {
            extraData.writeInt(itemHandlerSize);
            extraData.writeBoolean(hand == Hand.MAIN_HAND);
        });
    }
}
