package de.cas_ual_ty.ydm.duel;

public enum DuelState
{
    IDLE, DUELING, SIDING, END;
    
    public static final DuelState[] VALUES = DuelState.values();
    
    public static DuelState getFromIndex(int index)
    {
        return DuelState.VALUES[index];
    }
    
    static
    {
        int index = 0;
        for(DuelState duelState : DuelState.VALUES)
        {
            duelState.index = index++;
        }
    }
    
    private int index;
    
    public int getIndex()
    {
        return this.index;
    }
}
