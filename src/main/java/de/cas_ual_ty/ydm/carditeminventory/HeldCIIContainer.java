package de.cas_ual_ty.ydm.carditeminventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public abstract class HeldCIIContainer extends CIIContainer
{
    protected final InteractionHand hand;
    protected final ItemStack itemStack;
    
    public HeldCIIContainer(MenuType<?> type, int id, Inventory playerInventoryIn, IItemHandler itemHandler, InteractionHand hand)
    {
        super(type, id, playerInventoryIn, itemHandler);
        this.hand = hand;
        itemStack = player.getItemInHand(hand);
    }
    
    public HeldCIIContainer(MenuType<?> type, int id, Inventory playerInventoryIn, FriendlyByteBuf extraData)
    {
        this(type, id, playerInventoryIn, new ItemStackHandler(extraData.readInt()), extraData.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }
    
    @Override
    protected void createBottomSlots(Inventory playerInventoryIn)
    {
        if(hand == InteractionHand.OFF_HAND)
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
                
                if(id == playerInventoryIn.selected)
                {
                    addSlot(new Slot(playerInventoryIn, id, 8 + j1 * 18, 103 + l * 18 + i)
                    {
                        @Override
                        public boolean mayPickup(Player playerIn)
                        {
                            return false;
                        }
                    });
                }
                else
                {
                    addSlot(new Slot(playerInventoryIn, id, 8 + j1 * 18, 103 + l * 18 + i));
                }
            }
        }
        
        for(int i1 = 0; i1 < 9; ++i1)
        {
            id = i1;
            
            if(id == playerInventoryIn.selected)
            {
                addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i)
                {
                    @Override
                    public boolean mayPickup(Player playerIn)
                    {
                        return false;
                    }
                });
            }
            else
            {
                addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i));
            }
        }
    }
    
    public static void openGui(Player player, InteractionHand hand, int itemHandlerSize, MenuProvider p)
    {
        NetworkHooks.openScreen((ServerPlayer) player, p, (extraData) ->
        {
            extraData.writeInt(itemHandlerSize);
            extraData.writeBoolean(hand == InteractionHand.MAIN_HAND);
        });
    }
}
