package de.cas_ual_ty.ydm.duel.action;

import net.minecraft.network.PacketBuffer;

public class ShowZoneAction extends SingleZoneAction
{
    public ShowZoneAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType, sourceZoneId);
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
