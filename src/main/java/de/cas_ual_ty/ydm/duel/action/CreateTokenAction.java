package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.FriendlyByteBuf;

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
    
    public CreateTokenAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), DuelMessageUtility.decodeCardPosition(buf), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeCardPosition(destinationCardPosition, buf);
        DuelMessageUtility.encodeZoneOwner(player, buf);
    }
    
    @Override
    public void doAction()
    {
        token = new DuelCard(card.getCardHolder(), true, destinationCardPosition, card.getOwner());
        destinationZone.addTopCard(player, token);
    }
    
    @Override
    public void undoAction()
    {
        destinationZone.removeCard(token);
    }
}
