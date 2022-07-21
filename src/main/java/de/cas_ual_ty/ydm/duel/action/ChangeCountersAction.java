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
        buf.writeInt(counterChange);
    }
    
    @Override
    public void doAction()
    {
        previousCounters = sourceZone.getCounters();
        sourceZone.changeCounters(counterChange);
        newCounters = sourceZone.getCounters();
    }
    
    @Override
    public void undoAction()
    {
        sourceZone.setCounters(previousCounters);
    }
    
    @Override
    public void redoAction()
    {
        sourceZone.setCounters(newCounters);
    }
}
