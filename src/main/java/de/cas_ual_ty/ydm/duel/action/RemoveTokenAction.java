package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

public class RemoveTokenAction extends DualZoneAction
{
    public ZoneOwner player;
    
    public RemoveTokenAction(ActionType actionType, byte zoneId, short cardIndex, byte destinationZoneId, ZoneOwner player)
    {
        super(actionType, zoneId, cardIndex, destinationZoneId);
        this.player = player;
    }
    
    public RemoveTokenAction(ActionType actionType, Zone sourceZone, DuelCard card, Zone destinationZone, ZoneOwner player)
    {
        this(actionType, sourceZone.index, sourceZone.getCardIndexShort(card), destinationZone.index, player);
    }
    
    public RemoveTokenAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeZoneOwner(this.player, buf);
    }
    
    @Override
    public void doAction()
    {
        this.sourceZone.removeCard(this.card);
    }
    
    @Override
    public void undoAction()
    {
        this.sourceZone.addCard(this.player, this.card, this.sourceCardIndex);
    }
}
