package de.cas_ual_ty.ydm.card;

public class CardHolder implements ICardHolder
{
    public Card card;
    public byte overriddenImageIndex;
    public Rarity overriddenRarity;
    
    public CardHolder(Card card)
    {
        this.card = card;
        this.overriddenImageIndex = -1;
        this.overriddenRarity = null;
    }
    
    protected CardHolder()
    {
    }
    
    @Override
    public Card getCard()
    {
        return this.card;
    }
    
    @Override
    public void setCard(Card card)
    {
        this.card = card;
    }
    
    @Override
    public void overrideImageIndex(byte imageIndex)
    {
        this.overriddenImageIndex = imageIndex;
    }
    
    @Override
    public byte getOverriddenImageIndex()
    {
        return this.overriddenImageIndex;
    }
    
    @Override
    public void overrideRarity(Rarity rarity)
    {
        this.overriddenRarity = rarity;
    }
    
    @Override
    public Rarity getOverriddenRarity()
    {
        return this.overriddenRarity;
    }
}
