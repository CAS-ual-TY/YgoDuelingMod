package de.cas_ual_ty.ydm.cardinventory;

import java.util.UUID;

import de.cas_ual_ty.ydm.util.JsonKeys;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.nbt.CompoundNBT;

public abstract class UUIDCardInventoryManager extends JsonCardInventoryManager
{
    private UUID uuid;
    
    public UUIDCardInventoryManager()
    {
        this.uuid = null;
    }
    
    public UUID getUUID()
    {
        return this.uuid;
    }
    
    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
    }
    
    public void generateUUIDIfNull()
    {
        if(this.uuid == null)
        {
            this.uuid = YdmUtil.createRandomUUID();
        }
    }
    
    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        if(nbt.hasUniqueId(JsonKeys.UUID))
        {
            this.uuid = nbt.getUniqueId(JsonKeys.UUID);
        }
    }
    
    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        if(this.getUUID() != null)
        {
            nbt.putUniqueId(JsonKeys.UUID, this.getUUID());
        }
    }
}
