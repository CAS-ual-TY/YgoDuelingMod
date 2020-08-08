package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.DuelCard;
import de.cas_ual_ty.ydm.duel.PlayField;
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
        buf.writeByte(this.sourceZoneId);
        buf.writeShort(this.sourceCardIndex);
    }
    
    @Override
    public void init(PlayField playField)
    {
        super.init(playField);
        this.card = this.sourceZone.getCard(this.sourceCardIndex);
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
