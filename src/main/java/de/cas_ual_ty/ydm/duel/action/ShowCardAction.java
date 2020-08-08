package de.cas_ual_ty.ydm.duel.action;

import net.minecraft.network.PacketBuffer;

public class ShowCardAction extends SingleCardAction
{
    public ShowCardAction(ActionType actionType, byte sourceZoneId, short sourceCardIndex)
    {
        super(actionType, sourceZoneId, sourceCardIndex);
    }
    
    public ShowCardAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void doAction()
    {
        // TODO show card action
    }
}
