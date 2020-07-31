package de.cas_ual_ty.ydm.cardbinder;

import java.io.File;
import java.util.UUID;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardsManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CardBinderCardsManager extends UUIDCardsManager
{
    public CardBinderCardsManager()
    {
        super();
    }
    
    @Override
    protected File getFile()
    {
        this.generateUUIDIfNull();
        return CardBinderCardsManager.getBinderFile(this.getUUID());
    }
    
    public static File getBinderFile(UUID uuid)
    {
        return new File(YDM.bindersFolder, uuid.toString() + ".json");
    }
    
    public static class Storage implements IStorage<CardBinderCardsManager>
    {
        @Override
        public INBT writeNBT(Capability<CardBinderCardsManager> capability, CardBinderCardsManager instance, Direction side)
        {
            CompoundNBT nbt = new CompoundNBT();
            instance.writeToNBT(nbt);
            return nbt;
        }
        
        @Override
        public void readNBT(Capability<CardBinderCardsManager> capability, CardBinderCardsManager instance, Direction side, INBT nbt)
        {
            instance.readFromNBT((CompoundNBT)nbt);
        }
    }
}
