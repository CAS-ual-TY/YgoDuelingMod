package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ViewZoneAction extends SingleZoneAction implements IAnnouncedAction
{
    public ViewZoneAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType, sourceZoneId);
    }
    
    public ViewZoneAction(ActionType actionType, Zone sourceZone)
    {
        super(actionType, sourceZone.index);
    }
    
    public ViewZoneAction(ActionType actionType, PacketBuffer buf)
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
        return this.actionType.getLocalKey();
    }
    
    @Override
    public Zone getFieldAnnouncementZone()
    {
        return this.sourceZone;
    }
}
