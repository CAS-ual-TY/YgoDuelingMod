package de.cas_ual_ty.ydm.duel.action;

import java.util.List;

import de.cas_ual_ty.ydm.duel.DuelCard;
import de.cas_ual_ty.ydm.duel.Zone;

public class ShuffleAction extends Action
{
    protected List<DuelCard> before;
    protected List<DuelCard> after;
    
    public ShuffleAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    public void doAction()
    {
        this.before = this.getFrom().shuffle();
        this.after = this.getFrom().getCardsList();
    }
    
    @Override
    public void undoAction()
    {
        this.getFrom().setCardsList(this.before);
    }
    
    @Override
    public void redoAction()
    {
        this.getTo().setCardsList(this.after);
    }
    
}
