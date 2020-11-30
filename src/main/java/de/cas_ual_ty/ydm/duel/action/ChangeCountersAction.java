package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ChangeCountersAction extends SingleZoneAction
{
    public int counterChange;
    
    protected int newCounters;
    protected int previousCounters;
    
    public ChangeCountersAction(ActionType actionType, byte sourceZoneId, int counterChange)
    {
        super(actionType, sourceZoneId);
        this.counterChange = counterChange;
    }
    
    public ChangeCountersAction(ActionType actionType, Zone sourceZone, int counterChange)
    {
        this(actionType, sourceZone.index, counterChange);
    }
    
    public ChangeCountersAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readInt());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeInt(this.counterChange);
    }
    
    @Override
    public void doAction()
    {
        this.previousCounters = this.sourceZone.getCounters();
        this.sourceZone.changeCounters(this.counterChange);
        this.newCounters = this.sourceZone.getCounters();
    }
    
    @Override
    public void undoAction()
    {
        this.sourceZone.setCounters(this.previousCounters);
    }
    
    @Override
    public void redoAction()
    {
        this.sourceZone.setCounters(this.newCounters);
    }
}
