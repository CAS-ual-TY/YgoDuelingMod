package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public abstract class SingleZoneAction extends Action
{
    public byte sourceZoneId;
    
    public Zone sourceZone;
    
    public SingleZoneAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType);
        this.sourceZoneId = sourceZoneId;
    }
    
    public SingleZoneAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeByte(sourceZoneId);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        if(sourceZoneId != -1)
        {
            sourceZone = playField.getZone(sourceZoneId);
        }
        else
        {
            sourceZone = null;
        }
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
