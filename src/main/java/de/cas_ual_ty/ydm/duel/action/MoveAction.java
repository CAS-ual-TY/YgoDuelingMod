package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.Zone;

public abstract class MoveAction extends CardAction
{
    protected int toIndex;
    
    public MoveAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    public void doAction()
    {
        /*
         * cardIndex is the index of the card in the from-zone
         * toIndex is the new index in the to-zone
         */
        this.doMoveAction();
        this.toIndex = this.getTo().getCardIndex(this.getCard());
    }
    
    protected abstract void doMoveAction();
    
    @Override
    public void undoAction()
    {
        this.getTo().removeCard(this.toIndex);
        this.getFrom().addCard(this.getCard(), this.getCardIndex());
    }
    
    @Override
    public void redoAction()
    {
        this.getFrom().removeCard(this.getCardIndex());
        this.getTo().addCard(this.getCard(), this.toIndex);
    }
}
