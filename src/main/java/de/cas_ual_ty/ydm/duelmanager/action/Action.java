package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.playfield.PlayField;
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
        // actionType is already encoded. See DuelMessages class
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
