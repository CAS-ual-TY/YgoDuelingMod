package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.PlayField;
import de.cas_ual_ty.ydm.duelmanager.Zone;
import net.minecraft.network.PacketBuffer;

public abstract class MoveAction extends SingleCardAction
{
    public byte destinationZoneId;
    public CardPosition destinationCardPosition;
    
    public Zone destinationZone;
    public CardPosition sourceCardPosition;
    
    public short destinationCardIndex;
    
    public MoveAction(ActionType actionType, byte zoneId, short cardIndex, byte destinationZoneId, CardPosition destinationCardPosition)
    {
        super(actionType, zoneId, cardIndex);
        this.destinationZoneId = destinationZoneId;
        this.destinationCardPosition = destinationCardPosition;
    }
    
    public MoveAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), CardPosition.getFromIndex(buf.readByte()));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(this.destinationZoneId);
        buf.writeByte(this.destinationCardPosition.getIndex());
    }
    
    @Override
    public void init(PlayField playField)
    {
        super.init(playField);
        this.destinationZone = playField.getZone(this.destinationZoneId);
        this.sourceCardPosition = this.card.getCardPosition();
    }
    
    @Override
    public void doAction()
    {
        /*
         * cardIndex is the index of the card in the from-zone
         * toIndex is the new index in the to-zone
         */
        
        this.doMoveAction();
        this.card.setPosition(this.destinationCardPosition);
        this.destinationCardIndex = this.destinationZone.getCardIndexShort(this.card);
    }
    
    protected abstract void doMoveAction();
    
    @Override
    public void undoAction()
    {
        this.destinationZone.removeCard(this.destinationCardIndex);
        this.card.setPosition(this.sourceCardPosition);
        this.sourceZone.addCard(this.card, this.sourceCardIndex);
    }
    
    @Override
    public void redoAction()
    {
        this.sourceZone.removeCard(this.sourceCardIndex);
        this.card.setPosition(this.destinationCardPosition);
        this.destinationZone.addCard(this.card, this.destinationCardIndex);
    }
}
