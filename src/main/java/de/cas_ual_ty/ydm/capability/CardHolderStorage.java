package de.cas_ual_ty.ydm.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CardHolderStorage implements IStorage<ICardHolder>
{
    @Override
    public INBT writeNBT(Capability<ICardHolder> capability, ICardHolder instance, Direction side)
    {
        CompoundNBT nbt = new CompoundNBT();
        
        
        return nbt;
    }
    
    @Override
    public void readNBT(Capability<ICardHolder> capability, ICardHolder instance, Direction side, INBT nbt)
    {
        
    }
}
