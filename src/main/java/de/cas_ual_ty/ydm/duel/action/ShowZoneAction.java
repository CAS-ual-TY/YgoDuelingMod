package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ShowZoneAction extends SingleZoneAction implements IAnnouncedAction
{
    public ShowZoneAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType, sourceZoneId);
    }
    
    public ShowZoneAction(ActionType actionType, Zone sourceZone)
    {
        super(actionType, sourceZone.index);
    }
    
    public ShowZoneAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void doAction()
    {
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return actionType.getLocalKey();
    }
    
    @Override
    public Zone getFieldAnnouncementZone()
    {
        return sourceZone;
    }
}
