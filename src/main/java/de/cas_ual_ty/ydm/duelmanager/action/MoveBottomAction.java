package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class MoveBottomAction extends MoveAction
{
    public MoveBottomAction(ActionType actionType, byte zoneId, short cardIndex, byte zoneDestinationId, CardPosition destinationCardPosition)
    {
        super(actionType, zoneId, cardIndex, zoneDestinationId, destinationCardPosition);
    }
    
    public MoveBottomAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, CardPosition destinationCardPosition)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, destinationCardPosition);
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
