package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
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
    
    // not called by superclasses
    public void writeToBuf(PacketBuffer buf)
    {
        // actionType is already encoded. See DuelMessages class
    }
    
    // not called by superclasses
    public void initServer(PlayField playField)
    {
    }
    
    // not called by superclasses
    public void initClient(PlayField playField)
    {
        initServer(playField);
    }
    
    public abstract void doAction();
    
    public abstract void undoAction();
    
    public abstract void redoAction();
    
    public ActionType getActionType()
    {
        return actionType;
    }
}
