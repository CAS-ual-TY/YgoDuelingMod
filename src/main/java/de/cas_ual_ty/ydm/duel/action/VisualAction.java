package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public abstract class VisualAction extends Action
{
    // eg. for attacking indicators, showing cards etc.
    public VisualAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
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
