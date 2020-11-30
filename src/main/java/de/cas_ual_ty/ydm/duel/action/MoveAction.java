package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

public abstract class MoveAction extends DualZoneAction
{
    public CardPosition destinationCardPosition;
    
    public CardPosition sourceCardPosition;
    
    public short destinationCardIndex;
    
    public ZoneOwner player;
    
    public MoveAction(ActionType actionType, byte zoneId, short cardIndex, byte destinationZoneId, CardPosition destinationCardPosition, ZoneOwner player)
    {
        super(actionType, zoneId, cardIndex, destinationZoneId);
        this.destinationCardPosition = destinationCardPosition;
        this.player = player;
    }
    
    public MoveAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), CardPosition.getFromIndex(buf.readByte()), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(this.destinationCardPosition.getIndex());
        DuelMessageUtility.encodeZoneOwner(this.player, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        this.sourceCardPosition = this.card.getCardPosition();
    }
    
    @Override
    public void doAction()
    {
        /*
         * cardIndex is the index of the card in the from-zone
         * toIndex is the new index in the to-zone
         */
        
        // split this into single parts, for animations
        this.removeCardFromZone();
        this.addCard();
        this.finish();
    }
    
    public abstract void addCard();
    
    public void finish()
    {
        this.card.setPosition(this.destinationCardPosition);
        this.destinationCardIndex = this.destinationZone.getCardIndexShort(this.card);
    }
    
    @Override
    public void undoAction()
    {
        this.destinationZone.removeCard(this.destinationCardIndex);
        this.card.setPosition(this.sourceCardPosition);
        this.sourceZone.addCard(this.player, this.card, this.sourceCardIndex);
    }
    
    @Override
    public void redoAction()
    {
        this.sourceZone.removeCard(this.sourceCardIndex);
        this.card.setPosition(this.destinationCardPosition);
        this.destinationZone.addCard(this.player, this.card, this.destinationCardIndex);
    }
}
