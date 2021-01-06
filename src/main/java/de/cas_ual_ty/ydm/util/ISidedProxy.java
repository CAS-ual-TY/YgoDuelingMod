package de.cas_ual_ty.ydm.util;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.set.CardSet;
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
    
    public default String addCardInfoTag(String imageName)
    {
        return null;
    }
    
    public default String addCardItemTag(String imageName)
    {
        return null;
    }
    
    public default String addCardMainTag(String imageName)
    {
        return null;
    }
    
    public default String addSetInfoTag(String imageName)
    {
        return null;
    }
    
    public default String addSetItemTag(String imageName)
    {
        return null;
    }
    
    public default String getCardInfoReplacementImage(Properties properties, byte imageIndex)
    {
        return null;
    }
    
    public default String getCardMainReplacementImage(Properties properties, byte imageIndex)
    {
        return null;
    }
    
    public default String getSetInfoReplacementImage(CardSet set)
    {
        return null;
    }
    
    public default boolean continueTasks()
    {
        return YDM.continueTasks;
    }
    
    public default boolean forceTaskStop()
    {
        return YDM.forceTaskStop;
    }
}
