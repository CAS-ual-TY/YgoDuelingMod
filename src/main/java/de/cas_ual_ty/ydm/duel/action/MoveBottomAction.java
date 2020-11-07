package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

public class MoveBottomAction extends MoveAction
{
    public MoveBottomAction(ActionType actionType, byte zoneId, short cardIndex, byte zoneDestinationId, CardPosition destinationCardPosition, ZoneOwner player)
    {
        super(actionType, zoneId, cardIndex, zoneDestinationId, destinationCardPosition, player);
    }
    
    public MoveBottomAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, CardPosition destinationCardPosition, ZoneOwner player)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, destinationCardPosition, player);
    }
    
    public MoveBottomAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void addCard()
    {
        this.destinationZone.addBottomCard(this.player, this.card);
    }
}
