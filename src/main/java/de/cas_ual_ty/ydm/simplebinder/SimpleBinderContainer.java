package de.cas_ual_ty.ydm.simplebinder;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SimpleBinderContainer extends HeldCIIContainer
{
    public SimpleBinderContainer(MenuType<?> type, int id, Inventory playerInventoryIn, YDMItemHandler itemHandler, InteractionHand hand)
    {
        super(type, id, playerInventoryIn, itemHandler, hand);
    }
    
    public SimpleBinderContainer(MenuType<?> type, int id, Inventory playerInventoryIn, FriendlyByteBuf extraData)
    {
        super(type, id, playerInventoryIn, extraData);
    }
    
    @Override
    public boolean canPutStack(ItemStack itemStack)
    {
        return itemStack.getItem() == YdmItems.CARD.get();
    }
}
