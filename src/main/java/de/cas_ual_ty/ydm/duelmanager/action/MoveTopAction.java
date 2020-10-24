package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class MoveTopAction extends MoveAction
{
    public MoveTopAction(ActionType actionType, byte zoneId, short cardIndex, byte zoneDestinationId, CardPosition destinationCardPosition)
    {
        super(actionType, zoneId, cardIndex, zoneDestinationId, destinationCardPosition);
    }
    
    public MoveTopAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, CardPosition destinationCardPosition)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, destinationCardPosition);
    }
    
    public MoveTopAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    protected void doMoveAction()
    {
        this.sourceZone.removeCard(this.sourceCardIndex);
        this.destinationZone.addTopCard(this.card);
    }
}
