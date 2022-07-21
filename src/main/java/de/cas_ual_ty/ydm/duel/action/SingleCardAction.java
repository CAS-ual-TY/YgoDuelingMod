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
        sourceCardIndex = sourceCardId;
    }
    
    public SingleCardAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeShort(sourceCardIndex);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        if(sourceCardIndex == -1 || sourceZone == null)
        {
            card = null;
        }
        else
        {
            card = sourceZone.getCard(sourceCardIndex);
        }
    }
    
    public void removeCardFromZone()
    {
        // dont use index here
        // zone might have changed, index might catch different card
        sourceZone.removeCard(card);
    }
    
    @Override
    public void undoAction()
    {
        doAction();
    }
    
    @Override
    public void redoAction()
    {
        doAction();
    }
}
