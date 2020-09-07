package de.cas_ual_ty.ydm.duelmanager.action;

import net.minecraft.network.PacketBuffer;

public class ViewZoneAction extends SingleZoneAction
{
    public ViewZoneAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType, sourceZoneId);
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
