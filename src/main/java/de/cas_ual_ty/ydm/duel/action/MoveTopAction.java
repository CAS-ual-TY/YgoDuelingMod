package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

public class MoveTopAction extends MoveAction
{
    public MoveTopAction(ActionType actionType, byte zoneId, short cardIndex, byte zoneDestinationId, CardPosition destinationCardPosition, ZoneOwner player)
    {
        super(actionType, zoneId, cardIndex, zoneDestinationId, destinationCardPosition, player);
    }
    
    public MoveTopAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, CardPosition destinationCardPosition, ZoneOwner player)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, destinationCardPosition, player);
    }
    
    public MoveTopAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void addCard()
    {
        this.destinationZone.addTopCard(this.player, this.card);
    }
}
