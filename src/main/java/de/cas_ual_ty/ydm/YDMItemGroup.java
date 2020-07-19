package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.registries.YDMItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class YDMItemGroup extends ItemGroup
{
    public YDMItemGroup(String label)
    {
        super(label);
    }
    
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(YDMItems.CARD_BACK);
    }
}
