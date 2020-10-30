package de.cas_ual_ty.ydm.duelmanager.playfield;

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
    
    public CardPosition getCardPosition()
    {
        return this.position;
    }
    
    public ZoneOwner getOwner()
    {
        return this.owner;
    }
    
    @Override
    public String toString()
    {
        return "[" + this.owner + ": " + this.cardHolder.toString() + "]";
    }
}
