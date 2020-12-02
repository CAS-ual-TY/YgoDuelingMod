package de.cas_ual_ty.ydm.duel;

public enum DuelPhase
{
    DP("dp"), SP("sp"), M1("m1"), BP("bp"), M2("m2"), EP("ep");
    
    public static final DuelPhase[] VALUES = DuelPhase.values();
    
    public static DuelPhase getFromIndex(byte index)
    {
        return DuelPhase.VALUES[index];
    }
    
    public static final byte FIRST_INDEX;
    public static final byte LAST_INDEX;
    
    static
    {
        byte index = 0;
        for(DuelPhase duelPhase : DuelPhase.VALUES)
        {
            duelPhase.index = index++;
        }
        
        FIRST_INDEX = 0;
        LAST_INDEX = (byte)(DuelPhase.VALUES.length - 1);
    }
    
    private byte index;
    public final String local;
    
    private DuelPhase(String local)
    {
        this.local = local;
    }
    
    public byte getIndex()
    {
        return this.index;
    }
    
    public boolean isFirst()
    {
        return this.index == DuelPhase.FIRST_INDEX;
    }
    
    public boolean isLast()
    {
        return this.index == DuelPhase.LAST_INDEX;
    }
}
