package de.cas_ual_ty.ydm.cardinventory;

import de.cas_ual_ty.ydm.util.JsonKeys;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public abstract class UUIDCardsManager extends JsonCardsManager
{
    private UUID uuid;
    
    public UUIDCardsManager()
    {
        uuid = null;
    }
    
    public UUID getUUID()
    {
        return uuid;
    }
    
    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
    }
    
    public void generateUUIDIfNull()
    {
        if(uuid == null)
        {
            uuid = YdmUtil.createRandomUUID();
        }
    }
    
    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        if(nbt.hasUUID(JsonKeys.UUID))
        {
            uuid = nbt.getUUID(JsonKeys.UUID);
        }
    }
    
    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        if(getUUID() != null)
        {
            nbt.putUUID(JsonKeys.UUID, getUUID());
        }
    }
}
