package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public abstract class Action
{
    public final ActionType actionType;
    public final Zone from;
    public final Zone to;
    public final int cardIndex;
    
    public Action(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        this.actionType = actionType;
        this.from = from;
        this.to = to;
        this.cardIndex = cardIndex;
    }
    
    public abstract void doAction();
    
    public abstract void undoAction();
    
    public abstract void redoAction();
    
    public ActionType getActionType()
    {
        return this.actionType;
    }
    
    public Zone getFrom()
    {
        return this.from;
    }
    
    public Zone getTo()
    {
        return this.to;
    }
    
    public int getCardIndex()
    {
        return this.cardIndex;
    }
}
