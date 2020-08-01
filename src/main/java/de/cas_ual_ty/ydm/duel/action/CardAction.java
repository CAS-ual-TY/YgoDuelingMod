package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.DuelCard;
import de.cas_ual_ty.ydm.duel.Zone;

public abstract class CardAction extends Action
{
    public final DuelCard card;
    
    public CardAction(ActionType actionType, Zone from, Zone to, int cardIndex)
    {
        super(actionType, from, to, cardIndex);
        this.card = this.getFrom().getCard(this.getCardIndex());
    }
    
    public DuelCard getCard()
    {
        return this.card;
    }
}
