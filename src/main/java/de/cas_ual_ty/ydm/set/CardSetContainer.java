package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandler;

public class CardSetContainer extends HeldCIIContainer
{
    public CardSetContainer(MenuType<?> type, int id, Inventory playerInventoryIn, YDMItemHandler itemHandler, InteractionHand hand)
    {
        super(type, id, playerInventoryIn, itemHandler, hand);
    }
    
    public CardSetContainer(MenuType<?> type, int id, Inventory playerInventoryIn, FriendlyByteBuf extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public void removed(Player player)
    {
        super.removed(player);
        
        for(int i = 0; i < itemHandler.getSlots(); i++)
        {
            if(!itemHandler.getStackInSlot(i).isEmpty())
            {
                return;
            }
        }
        
        // item handler is empty
        // delete empty pack
        itemStack.shrink(itemStack.getCount());
    }
}
