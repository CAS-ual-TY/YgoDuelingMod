package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.card.CardHolder;

public class DuelCard
{
    public CardHolder cardHolder;
    public boolean isToken;
    public CardPosition position;
    public ZoneOwner owner;
    
    public DuelCard(CardHolder cardHolder, boolean isToken, CardPosition position, ZoneOwner owner)
    {
        this.cardHolder = cardHolder;
        this.isToken = isToken;
        this.position = position;
        this.owner = owner;
    }
    
    public DuelCard(CardHolder cardHolder, ZoneOwner owner)
    {
        this(cardHolder, false, CardPosition.FD, owner);
    }
    
    public DuelCard setToken()
    {
        isToken = true;
        return this;
    }
    
    public DuelCard setPosition(CardPosition position)
    {
        this.position = position;
        return this;
    }
    
    public CardHolder getCardHolder()
    {
        return cardHolder;
    }
    
    public boolean getIsToken()
    {
        return isToken;
    }
    
    public CardPosition getCardPosition()
    {
        return position;
    }
    
    public ZoneOwner getOwner()
    {
        return owner;
    }
    
    @Override
    public String toString()
    {
        return "[" + owner + ": " + cardHolder.toString() + "]";
    }
}
