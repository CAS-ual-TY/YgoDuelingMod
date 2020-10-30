package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ViewZoneAction extends SingleZoneAction
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
        // TODO view zone action
        // log it
    }
}
