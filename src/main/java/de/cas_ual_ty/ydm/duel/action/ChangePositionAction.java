package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ChangePositionAction extends SingleCardAction
{
    public CardPosition sourceCardPosition;
    public CardPosition destinationCardPosition;
    
    public ChangePositionAction(ActionType actionType, byte sourceZoneId, short sourceCardId, CardPosition destinationCardPosition)
    {
        super(actionType, sourceZoneId, sourceCardId);
        this.destinationCardPosition = destinationCardPosition;
    }
    
    public ChangePositionAction(ActionType actionType, Zone zone, DuelCard sourceCard, CardPosition targetCardPosition)
    {
        this(actionType, zone.index, zone.getCardIndexShort(sourceCard), targetCardPosition);
    }
    
    public ChangePositionAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), DuelMessageUtility.decodeCardPosition(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeCardPosition(this.destinationCardPosition, buf);
    }
    
    @Override
    public void init(PlayField playField)
    {
        super.init(playField);
        this.sourceCardPosition = this.sourceZone.getCard(this.sourceCardIndex).getCardPosition();
    }
    
    @Override
    public void doAction()
    {
        this.sourceZone.getCard(this.sourceCardIndex).setPosition(this.destinationCardPosition);
    }
    
    @Override
    public void undoAction()
    {
        this.sourceZone.getCard(this.sourceCardIndex).setPosition(this.sourceCardPosition);
    }
}
