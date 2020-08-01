package de.cas_ual_ty.ydm.duel;

public enum ZoneType
{
    HAND, DECK, SPELL_TRAP, SPELL_TRAP_PENDULUM, EXTRA_DECK, GRAVEYARD(true), MONSTER(false, false), FIELD_SPELL, BANISHED(true), EXTRA_MONSTER;
    
    public final boolean straightOnly;
    public final boolean isStrict;
    
    private ZoneType()
    {
        this(true, false);
    }
    
    private ZoneType(boolean isStrict)
    {
        this(true, isStrict);
    }
    
    private ZoneType(boolean straightOnly, boolean isStrict)
    {
        this.straightOnly = straightOnly;
        this.isStrict = isStrict;
    }
    
    public boolean getStraightOnly()
    {
        return this.straightOnly;
    }
    
    public boolean getIsStrict()
    {
        return this.isStrict;
    }
}
