package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;

public class CardSetContainer extends HeldCIIContainer
{
    public CardSetContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IItemHandler itemHandler, Hand hand)
    {
        super(type, id, playerInventoryIn, itemHandler, hand);
    }
    
    public CardSetContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, PacketBuffer extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public void saveItemHandler(PlayerEntity player, ItemStack itemStack, IItemHandler itemHandler)
    {
        YdmItems.OPENED_SET.setItemHandler(itemStack, itemHandler);
    }
}
