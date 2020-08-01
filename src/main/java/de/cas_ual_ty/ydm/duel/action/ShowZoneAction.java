package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public class ShowZoneAction extends VisualAction
{
    public ShowZoneAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    public void doAction()
    {
        // TODO show zone action
    }
}
