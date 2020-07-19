package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.registries.YdmItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class YdmItemGroup extends ItemGroup
{
    public YdmItemGroup(String label)
    {
        super(label);
    }
    
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(YdmItems.CARD_BACK);
    }
}
