package de.cas_ual_ty.ydm.duel.playfield;

public enum CardPosition
{
    ATK(true, true), DEF(false, true), SET(false, false), FD(true, false);
    
    public static final CardPosition[] VALUES = CardPosition.values();
    
    public static CardPosition getFromIndex(byte index)
    {
        return CardPosition.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(CardPosition cardPosition : CardPosition.VALUES)
        {
            cardPosition.index = index++;
        }
    }
    
    public final boolean isStraight;
    public final boolean isFaceUp;
    
    private byte index;
    
    CardPosition(boolean isStraight, boolean isFaceUp)
    {
        this.isStraight = isStraight;
        this.isFaceUp = isFaceUp;
    }
    
    public byte getIndex()
    {
        return index;
    }
    
    public CardPosition flip()
    {
        if(this == ATK)
        {
            return FD;
        }
        else if(this == DEF)
        {
            return SET;
        }
        else if(this == SET)
        {
            return DEF;
        }
        else //if(this == FACE_DOWN)
        {
            return ATK;
        }
    }
    
    public CardPosition rotate()
    {
        if(this == ATK)
        {
            return DEF;
        }
        else if(this == DEF)
        {
            return ATK;
        }
        else if(this == SET)
        {
            return FD;
        }
        else //if(this == FACE_DOWN)
        {
            return SET;
        }
    }
}
