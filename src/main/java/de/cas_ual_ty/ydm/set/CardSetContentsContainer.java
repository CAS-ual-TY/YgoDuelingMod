package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class CardSetContentsContainer extends CIIContainer
{
    public CardSetContentsContainer(MenuType<?> type, int id, Inventory playerInventoryIn, YDMItemHandler itemHandler)
    {
        super(type, id, playerInventoryIn, itemHandler);
    }
    
    public CardSetContentsContainer(MenuType<?> type, int id, Inventory playerInventoryIn, FriendlyByteBuf extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public boolean canPutStack(ItemStack itemStack)
    {
        return false;
    }
    
    @Override
    public boolean canTakeStack(Player player, ItemStack itemStack)
    {
        return false;
    }
}
