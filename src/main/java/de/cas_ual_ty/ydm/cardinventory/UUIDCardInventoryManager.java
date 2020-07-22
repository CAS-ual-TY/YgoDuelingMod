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
        if(this.uuid == null)
        {
            this.uuid = YdmUtil.createRandomUUID();
        }
        
        return this.uuid;
    }
    
    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        this.uuid = nbt.getUniqueId(JsonKeys.UUID);
    }
    
    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        nbt.putUniqueId(JsonKeys.UUID, this.uuid);
    }
}
