package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

public class CreateTokenAction extends DualZoneAction
{
    public CardPosition destinationCardPosition;
    public ZoneOwner player;
    
    public DuelCard token;
    
    public CreateTokenAction(ActionType actionType, byte zoneId, short cardIndex, byte destinationZoneId, CardPosition destinationCardPosition, ZoneOwner player)
    {
        super(actionType, zoneId, cardIndex, destinationZoneId);
        this.destinationCardPosition = destinationCardPosition;
        this.player = player;
    }
    
    public CreateTokenAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, CardPosition destinationCardPosition, ZoneOwner player)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, destinationCardPosition, player);
    }
    
    public CreateTokenAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), DuelMessageUtility.decodeCardPosition(buf), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeCardPosition(this.destinationCardPosition, buf);
        DuelMessageUtility.encodeZoneOwner(this.player, buf);
    }
    
    @Override
    public void doAction()
    {
        this.token = new DuelCard(this.card.getCardHolder(), true, this.destinationCardPosition, this.card.getOwner());
        this.destinationZone.addTopCard(this.player, this.token);
    }
    
    @Override
    public void undoAction()
    {
        this.destinationZone.removeCard(this.token);
    }
}
