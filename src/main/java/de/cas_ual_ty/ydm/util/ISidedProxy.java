package de.cas_ual_ty.ydm.util;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
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
    
    public default void init()
    {
        
    }
    
    public default void initFiles()
    {
        
    }
    
    @Nullable
    public default PlayerEntity getClientPlayer()
    {
        return null;
    }
}
