package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public class MoveTopAction extends MoveAction
{
    public MoveTopAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    protected void doMoveAction()
    {
        this.getFrom().removeCard(this.getCardIndex());
        this.getTo().addTopCard(this.getCard());
    }
}
