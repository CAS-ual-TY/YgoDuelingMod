package de.cas_ual_ty.ydm.cardbinder;

import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class UUIDHolder implements IUUIDHolder, INBTSerializable<StringNBT>
{
    public static final UUIDHolder NULL_HOLDER = new UUIDHolder()
    {
        @Override
        public UUID getUUID()
        {
            return null;
        }
    };
    
    protected UUID uuid;
    
    public UUIDHolder()
    {
        uuid = null;
    }
    
    @Override
    @Nullable
    public UUID getUUID()
    {
        return uuid;
    }
    
    @Override
    public void setUUID(UUID uuid)
    {
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
}
