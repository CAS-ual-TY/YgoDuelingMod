package de.cas_ual_ty.ydm.duelmanager;

public enum CardPosition
{
    ATK(true, true), DEF(false, true), SET(false, false), FACE_DOWN(true, false);
    
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
    
    private CardPosition(boolean isStraight, boolean isFaceUp)
    {
        this.isStraight = isStraight;
        this.isFaceUp = isFaceUp;
    }
    
    public byte getIndex()
    {
        return this.index;
    }
    
    public CardPosition flip()
    {
        if(this == ATK)
        {
            return FACE_DOWN;
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
}
