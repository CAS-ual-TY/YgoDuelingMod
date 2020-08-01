package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.DuelCard;
import de.cas_ual_ty.ydm.duel.Zone;

public class ShowCardAction extends VisualAction
{
    protected DuelCard card;
    
    public ShowCardAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
    }
    
    @Override
    public void doAction()
    {
        this.card = this.getFrom().getCard(this.getCardIndex());
        // TODO show card action
    }
}
