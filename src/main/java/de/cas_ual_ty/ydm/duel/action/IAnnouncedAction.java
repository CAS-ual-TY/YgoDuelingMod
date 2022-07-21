package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface IAnnouncedAction
{
    String getAnnouncementLocalKey();
    
    default IFormattableTextComponent getAnnouncement(ITextComponent playerName)
    {
        if(getFieldAnnouncementZone() == null)
        {
            return new TranslationTextComponent(getAnnouncementLocalKey());
        }
        else
        {
            return new TranslationTextComponent(getAnnouncementLocalKey()).append(": ").append(getFieldAnnouncementZone().getType().getLocal());
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
