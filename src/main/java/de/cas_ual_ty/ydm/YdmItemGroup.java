package de.cas_ual_ty.ydm;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class YdmItemGroup extends CreativeModeTab
{
    private Supplier<Item> supplier;
    
    public YdmItemGroup(String label, Supplier<Item> supplier)
    {
        super(label);
        this.supplier = supplier;
    }
    
    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(supplier.get());
    }
}
