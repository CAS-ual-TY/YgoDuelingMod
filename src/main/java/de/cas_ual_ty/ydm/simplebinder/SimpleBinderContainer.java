package de.cas_ual_ty.ydm.simplebinder;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;

public class SimpleBinderContainer extends HeldCIIContainer
{
    public SimpleBinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler, Hand hand)
    {
        super(type, id, playerInventoryIn, itemHandler, hand);
    }
    
    public SimpleBinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, PacketBuffer extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public boolean canPutStack(ItemStack itemStack)
    {
        return itemStack.getItem() == YdmItems.CARD;
    }
}
