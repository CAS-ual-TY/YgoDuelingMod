package de.cas_ual_ty.ydm;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class YdmItemGroup extends ItemGroup
{
    private Supplier<Item> supplier;
    
    public YdmItemGroup(String label, Supplier<Item> supplier)
    {
        super(label);
        this.supplier = supplier;
    }
    
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(this.supplier.get());
    }
}
