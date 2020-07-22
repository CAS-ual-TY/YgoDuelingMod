package de.cas_ual_ty.ydm.binder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CardBinderItem extends Item
{
    public CardBinderItem(Properties properties)
    {
        super(properties);
    }
    
    public BinderCardInventoryManager getInventoryManager(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY).orElseThrow(YdmUtil.throwNullCapabilityException());
    }
}
