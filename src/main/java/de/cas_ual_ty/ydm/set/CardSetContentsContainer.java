package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

public class CardSetContentsContainer extends CIIContainer
{
    public CardSetContentsContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler)
    {
        super(type, id, playerInventoryIn, itemHandler);
    }
    
    public CardSetContentsContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, PacketBuffer extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public boolean canPutStack(ItemStack itemStack)
    {
        return false;
    }
    
    @Override
    public boolean canTakeStack(PlayerEntity player, ItemStack itemStack)
    {
        return false;
    }
}
