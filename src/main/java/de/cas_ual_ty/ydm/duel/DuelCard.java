package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.card.CardHolder;

public class DuelCard
{
    public CardHolder cardHolder;
    public boolean isToken;
    public CardPosition position;
    public PlayerRole owner;
    
    public DuelCard(CardHolder cardHolder, boolean isToken, CardPosition position, PlayerRole owner)
    {
        this.cardHolder = cardHolder;
        this.isToken = isToken;
        this.position = position;
        this.owner = owner;
    }
    
    public DuelCard(CardHolder cardHolder, PlayerRole owner)
    {
        this(cardHolder, false, CardPosition.FACE_DOWN, owner);
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
    
    public PlayerRole getOwner()
    {
        return this.owner;
    }
}
