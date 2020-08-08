package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.PlayField;
import net.minecraft.network.PacketBuffer;

public abstract class Action
{
    public final ActionType actionType;
    
    public Action(ActionType actionType)
    {
        this.actionType = actionType;
    }
    
    public Action(ActionType actionType, PacketBuffer buf)
    {
        this(actionType);
    }
    
    public void writeToBuf(PacketBuffer buf)
    {
        buf.writeByte(this.actionType.getIndex());
    }
    
    public void init(PlayField playField)
    {
    }
    
    public abstract void doAction();
    
    public abstract void undoAction();
    
    public abstract void redoAction();
    
    public ActionType getActionType()
    {
        return this.actionType;
    }
}
