package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ShowZoneAction extends SingleZoneAction
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
        // TODO show zone action
    }
}
