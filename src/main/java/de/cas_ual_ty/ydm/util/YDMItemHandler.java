package de.cas_ual_ty.ydm.util;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class YDMItemHandler extends ItemStackHandler
{
    protected Supplier<CompoundNBT> nbtSupplier;
    
    public YDMItemHandler(Supplier<CompoundNBT> nbtSupplier)
    {
        super();
        this.nbtSupplier = nbtSupplier;
    }
    
    public YDMItemHandler(int size, Supplier<CompoundNBT> nbtSupplier)
    {
        super(size);
        this.nbtSupplier = nbtSupplier;
    }
    
    public YDMItemHandler(NonNullList<ItemStack> stacks, Supplier<CompoundNBT> nbtSupplier)
    {
        super(stacks);
        this.nbtSupplier = nbtSupplier;
    }
    
    public void load()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundNBT nbt = nbtSupplier.get();
            
            if(nbt.contains("item_handler_cap"))
            {
                INBT inbt = nbt.get("item_handler_cap");
                
                if(inbt instanceof CompoundNBT)
                {
                    deserializeNBT((CompoundNBT) inbt);
                }
            }
        }
    }
    
    public void save()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundNBT nbt = nbtSupplier.get();
            nbt.put("item_handler_cap", serializeNBT());
        }
    }
}
