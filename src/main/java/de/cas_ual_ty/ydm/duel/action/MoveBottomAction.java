package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.CardPosition;
import net.minecraft.network.PacketBuffer;

public class MoveBottomAction extends MoveAction
{
    public MoveBottomAction(ActionType actionType, byte zoneId, short cardIndex, byte zoneDestinationId, CardPosition destinationCardPosition)
    {
        super(actionType, zoneId, cardIndex, zoneDestinationId, destinationCardPosition);
    }
    
    public MoveBottomAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    protected void doMoveAction()
    {
        this.sourceZone.removeCard(this.sourceCardIndex);
        this.destinationZone.addBottomCard(this.card);
    }
}
