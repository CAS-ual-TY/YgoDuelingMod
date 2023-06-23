package de.cas_ual_ty.ydm.util;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class YDMItemHandler extends ItemStackHandler
{
    protected Supplier<CompoundTag> nbtSupplier;
    
    public YDMItemHandler(Supplier<CompoundTag> nbtSupplier)
    {
        super();
        this.nbtSupplier = nbtSupplier;
    }
    
    public YDMItemHandler(int size, Supplier<CompoundTag> nbtSupplier)
    {
        super(size);
        this.nbtSupplier = nbtSupplier;
    }
    
    public YDMItemHandler(NonNullList<ItemStack> stacks, Supplier<CompoundTag> nbtSupplier)
    {
        super(stacks);
        this.nbtSupplier = nbtSupplier;
    }
    
    public void load()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundTag nbt = nbtSupplier.get();
            
            if(nbt.contains("item_handler_cap"))
            {
                Tag inbt = nbt.get("item_handler_cap");
                
                if(inbt instanceof CompoundTag)
                {
                    deserializeNBT((CompoundTag) inbt);
                }
            }
        }
    }
    
    public void save()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundTag nbt = nbtSupplier.get();
            nbt.put("item_handler_cap", serializeNBT());
        }
    }
}
