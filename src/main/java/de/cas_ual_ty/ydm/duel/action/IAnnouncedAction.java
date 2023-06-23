package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;



public interface IAnnouncedAction
{
    String getAnnouncementLocalKey();
    
    default MutableComponent getAnnouncement(Component playerName)
    {
        if(getFieldAnnouncementZone() == null)
        {
            return Component.translatable(getAnnouncementLocalKey());
        }
        else
        {
            return Component.translatable(getAnnouncementLocalKey()).append(": ").append(getFieldAnnouncementZone().getType().getLocal());
        }
    }
    
    default boolean announceOnField()
    {
        return getFieldAnnouncementZone() != null;
    }
    
    default Zone getFieldAnnouncementZone()
    {
        return null;
    }
}
