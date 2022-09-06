package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class UUIDHolder implements IUUIDHolder, INBTSerializable<StringNBT>
{
    private static final CompoundNBT DUMMY_NBT = new CompoundNBT();
    public static final UUIDHolder NULL_HOLDER = new UUIDHolder(() -> DUMMY_NBT)
    {
        @Override
        public UUID getUUID()
        {
            return null;
        }
    };
    
    protected UUID uuid;
    protected Supplier<CompoundNBT> nbtSupplier;
    
    public UUIDHolder(Supplier<CompoundNBT> nbtSupplier)
    {
        uuid = null;
        this.nbtSupplier = nbtSupplier;
    }
    
    @Override
    @Nullable
    public UUID getUUID()
    {
        load();
        return uuid;
    }
    
    @Override
    public void setUUID(UUID uuid)
    {
        save();
        this.uuid = uuid;
    }
    
    @Override
    public StringNBT serializeNBT()
    {
        return uuid == null ? StringNBT.valueOf("") : StringNBT.valueOf(getUUID().toString());
    }
    
    @Override
    public void deserializeNBT(StringNBT nbt)
    {
        String uuid = nbt.getAsString();
        
        if(uuid.isEmpty())
        {
            this.uuid = null;
        }
        else
        {
            this.uuid = UUID.fromString(nbt.getAsString());
        }
    }
    
    public void load()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundNBT nbt = nbtSupplier.get();
            
            if(nbt.contains("uuid_cap"))
            {
                INBT inbt = nbt.get("uuid_cap");
                
                if(inbt instanceof StringNBT)
                {
                    deserializeNBT((StringNBT) inbt);
                }
            }
        }
    }
    
    public void save()
    {
        if(YDM.commonConfig.mohistWorkaround.get())
        {
            CompoundNBT nbt = nbtSupplier.get();
            nbt.put("uuid_cap", serializeNBT());
        }
    }
}
