package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.FriendlyByteBuf;

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
    
    public MoveAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte(), CardPosition.getFromIndex(buf.readByte()), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(destinationCardPosition.getIndex());
        DuelMessageUtility.encodeZoneOwner(player, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        sourceCardPosition = card.getCardPosition();
    }
    
    @Override
    public void doAction()
    {
        /*
         * cardIndex is the index of the card in the from-zone
         * toIndex is the new index in the to-zone
         */
        
        // split this into single parts, for animations
        removeCardFromZone();
        addCard();
        finish();
    }
    
    public abstract void addCard();
    
    public void finish()
    {
        card.setPosition(destinationCardPosition);
        destinationCardIndex = destinationZone.getCardIndexShort(card);
    }
    
    @Override
    public void undoAction()
    {
        destinationZone.removeCard(destinationCardIndex);
        card.setPosition(sourceCardPosition);
        sourceZone.addCard(player, card, sourceCardIndex);
    }
    
    @Override
    public void redoAction()
    {
        sourceZone.removeCard(sourceCardIndex);
        card.setPosition(destinationCardPosition);
        destinationZone.addCard(player, card, destinationCardIndex);
    }
}
