package de.cas_ual_ty.ydm.cardbinder;

import java.io.File;
import java.util.UUID;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CardBinderCardManager extends UUIDCardManager
{
    public CardBinderCardManager()
    {
        super();
    }
    
    @Override
    protected File getFile()
    {
        this.generateUUIDIfNull();
        return CardBinderCardManager.getBinderFile(this.getUUID());
    }
    
    public static File getBinderFile(UUID uuid)
    {
        return new File(YDM.bindersFolder, uuid.toString() + ".json");
    }
    
    public static class Storage implements IStorage<CardBinderCardManager>
    {
        @Override
        public INBT writeNBT(Capability<CardBinderCardManager> capability, CardBinderCardManager instance, Direction side)
        {
            CompoundNBT nbt = new CompoundNBT();
            instance.writeToNBT(nbt);
            return nbt;
        }
        
        @Override
        public void readNBT(Capability<CardBinderCardManager> capability, CardBinderCardManager instance, Direction side, INBT nbt)
        {
            instance.readFromNBT((CompoundNBT)nbt);
        }
    }
}
