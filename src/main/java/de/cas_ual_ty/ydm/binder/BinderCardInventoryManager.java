package de.cas_ual_ty.ydm.binder;

import java.io.File;
import java.util.UUID;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardInventoryManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class BinderCardInventoryManager extends UUIDCardInventoryManager
{
    public BinderCardInventoryManager()
    {
        super();
    }
    
    @Override
    protected File getFile()
    {
        return BinderCardInventoryManager.getBinderFile(this.getUUID());
    }
    
    public static File getBinderFile(UUID uuid)
    {
        return new File(YDM.bindersFolder, uuid.toString() + ".json");
    }
    
    public static class Storage implements IStorage<BinderCardInventoryManager>
    {
        @Override
        public INBT writeNBT(Capability<BinderCardInventoryManager> capability, BinderCardInventoryManager instance, Direction side)
        {
            CompoundNBT nbt = new CompoundNBT();
            instance.writeToNBT(nbt);
            return nbt;
        }
        
        @Override
        public void readNBT(Capability<BinderCardInventoryManager> capability, BinderCardInventoryManager instance, Direction side, INBT nbt)
        {
            instance.readFromNBT((CompoundNBT)nbt);
        }
    }
}
