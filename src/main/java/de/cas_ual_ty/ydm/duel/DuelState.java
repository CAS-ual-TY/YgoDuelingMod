package de.cas_ual_ty.ydm.duel;

public enum DuelState
{
    IDLE, DUELING, SIDING, END;
    
    public static final DuelState[] VALUES = DuelState.values();
    
    public static DuelState getFromIndex(byte index)
    {
        return DuelState.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(DuelState duelState : DuelState.VALUES)
        {
            duelState.index = index++;
        }
    }
    
    private byte index;
    
    public byte getIndex()
    {
        return this.index;
    }
}
