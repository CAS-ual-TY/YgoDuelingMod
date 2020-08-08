package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.PlayField;
import de.cas_ual_ty.ydm.duel.Zone;
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
        super.writeToBuf(buf);
        buf.writeByte(this.sourceZoneId);
    }
    
    @Override
    public void init(PlayField playField)
    {
        this.sourceZone = playField.getZone(this.sourceZoneId);
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
