package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.card.CardHolder;

public class DuelCard
{
    public CardHolder cardHolder;
    public boolean isToken;
    public CardPosition position;
    public Player owner;
    
    public DuelCard(CardHolder cardHolder, Player owner)
    {
        this.cardHolder = cardHolder;
        this.isToken = false;
        this.position = CardPosition.FACE_DOWN;
        this.owner = owner;
    }
    
    public DuelCard setToken()
    {
        this.isToken = true;
        return this;
    }
    
    public DuelCard setPosition(CardPosition position)
    {
        this.position = position;
        return this;
    }
    
    public CardHolder getCardHolder()
    {
        return this.cardHolder;
    }
    
    public boolean getIsToken()
    {
        return this.isToken;
    }
    
    public Player getOwner()
    {
        return this.owner;
    }
}
