package de.cas_ual_ty.ydm.duel;

public enum DuelState
{
    IDLE, PREPARING, DUELING, SIDING, END;
    
    /*
     * Idle: Nothing there, role selection open
     * Preparing: p1 chooses format, p1 chooses banlist, choose deck, ready up
     * Dueling: duh
     * Siding: duh
     * End: Match ended, winner is shown, add "rematch" and "new duel" button
     */
    
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
        return index;
    }
}
