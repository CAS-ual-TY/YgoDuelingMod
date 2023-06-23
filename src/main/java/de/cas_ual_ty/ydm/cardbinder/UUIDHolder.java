package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class UUIDHolder implements IUUIDHolder, INBTSerializable<StringTag>
{
    private static final CompoundTag DUMMY_NBT = new CompoundTag();
    public static final UUIDHolder NULL_HOLDER = new UUIDHolder(() -> DUMMY_NBT)
    {
        @Override
        public UUID getUUID()
        {
            return null;
        }
    };
    
    protected UUID uuid;
    protected Supplier<CompoundTag> nbtSupplier;
    
    public UUIDHolder(Supplier<CompoundTag> nbtSupplier)
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
        this.uuid = uuid;
        save();
    }
    
    @Override
    public StringTag serializeNBT()
    {
        return uuid == null ? StringTag.valueOf("") : StringTag.valueOf(getUUID().toString());
    }
    
    @Override
    public void deserializeNBT(StringTag nbt)
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
            UUID old = uuid;
            CompoundTag nbt = nbtSupplier.get();
            
            if(nbt.contains("uuid_cap"))
            {
                Tag inbt = nbt.get("uuid_cap");
                
                if(inbt instanceof StringTag)
                {
                    deserializeNBT((StringTag) inbt);
                }
            }
            
            if(uuid == null)
            {
                uuid = old;
            }
            
            save();
        }
    }
    
    public void save()
    {
        if(YDM.commonConfig.mohistWorkaround.get() && uuid != null)
        {
            CompoundTag nbt = nbtSupplier.get();
            nbt.put("uuid_cap", uuid == null ? StringTag.valueOf("") : StringTag.valueOf(uuid.toString()));
        }
    }
}
