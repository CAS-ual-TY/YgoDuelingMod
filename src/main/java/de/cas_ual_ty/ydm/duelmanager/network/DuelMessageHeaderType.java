package de.cas_ual_ty.ydm.duelmanager.network;

import java.util.function.Supplier;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class DuelMessageHeaderType extends ForgeRegistryEntry<DuelMessageHeaderType>
{
    public Supplier<DuelMessageHeader> factory;
    
    public DuelMessageHeaderType(Supplier<DuelMessageHeader> factory)
    {
        this.factory = factory;
    }
    
    public DuelMessageHeader createHeader()
    {
        return this.factory.get();
    }
}
