package de.cas_ual_ty.ydm.proxy;

import net.minecraftforge.eventbus.api.IEventBus;

public interface ISidedProxy
{
    public default void registerModEventListeners(IEventBus bus)
    {
    }
    
    public default void registerForgeEventListeners(IEventBus bus)
    {
    }
    
    public default void preInit()
    {
        
    }
}
