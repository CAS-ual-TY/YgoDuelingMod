package de.cas_ual_ty.ydm.duelmanager.action;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ActionType extends ForgeRegistryEntry<ActionType>
{
    public final ActionType.Factory factory;
    
    public ActionType(ActionType.Factory factory)
    {
        this.factory = factory;
    }
    
    public ActionType.Factory getFactory()
    {
        return this.factory;
    }
    
    public static interface Factory
    {
        Action create(ActionType type, PacketBuffer buf);
    }
}
