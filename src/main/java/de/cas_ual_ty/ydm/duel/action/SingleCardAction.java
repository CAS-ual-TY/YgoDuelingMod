package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import net.minecraft.network.PacketBuffer;

public abstract class SingleCardAction extends SingleZoneAction
{
    public short sourceCardIndex;
    
    public DuelCard card;
    
    public SingleCardAction(ActionType actionType, byte sourceZoneId, short sourceCardId)
    {
        super(actionType, sourceZoneId);
        this.sourceCardIndex = sourceCardId;
    }
    
    public SingleCardAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeShort(this.sourceCardIndex);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        this.card = this.sourceZone.getCard(this.sourceCardIndex);
    }
    
    public void removeCardFromZone()
    {
        // dont use index here
        // zone might have changed, index might catch different card
        this.sourceZone.removeCard(this.card);
    }
    
    @Override
    public void undoAction()
    {
        this.doAction();
    }
    
    @Override
    public void redoAction()
    {
        this.doAction();
    }
}
