package de.cas_ual_ty.ydm.util;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
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
    
    public default void displayPreparingDuelScreenAndRequestUpdate(DuelManager duelManager)
    {
        
    }
    
    @Nullable
    public default PlayerEntity getClientPlayer()
    {
        return null;
    }
    
    public default String addInfoTag(String imageName)
    {
        return null;
    }
    
    public default String addItemTag(String imageName)
    {
        return null;
    }
    
    public default String addMainTag(String imageName)
    {
        return null;
    }
    
    public default String getInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return null;
    }
    
    public default String getMainReplacementImage(Properties properties, byte imageIndex)
    {
        return null;
    }
}
