package de.cas_ual_ty.ydm.duel;

public enum CardPosition
{
    ATK(true, true), DEF(false, true), SET(false, false), FACE_DOWN(true, false);
    
    public final boolean isStraight;
    public final boolean isFaceUp;
    
    private CardPosition(boolean isStraight, boolean isFaceUp)
    {
        this.isStraight = isStraight;
        this.isFaceUp = isFaceUp;
    }
}
