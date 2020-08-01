package de.cas_ual_ty.ydm.duel;

public enum DuelState
{
    IDLE, DUELING, SIDING;
    
    public static final DuelState[] VALUES = DuelState.values();
    
    public DuelState getFromIndex(int index)
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
