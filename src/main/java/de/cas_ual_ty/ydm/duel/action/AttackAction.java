package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public class AttackAction extends VisualAction
{
    public AttackAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    public void doAction()
    {
        // TODO attack action
        // from zone to zone, no need for a specific card
    }
}
