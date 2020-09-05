package de.cas_ual_ty.ydm.duel;

import javax.annotation.Nullable;

public enum ZoneType
{
    HAND, DECK(CardPosition.FACE_DOWN), SPELL_TRAP, SPELL_TRAP_PENDULUM, EXTRA_DECK, GRAVEYARD(true), MONSTER(false, false), FIELD_SPELL, BANISHED(true), EXTRA_MONSTER, EXTRA;
    
    public final boolean straightOnly;
    public final boolean isStrict;
    
    @Nullable
    public final CardPosition defaultCardPosition;
    
    private ZoneType()
    {
        this((CardPosition)null);
    }
    
    private ZoneType(CardPosition defaultCardPosition)
    {
        this(true, false, defaultCardPosition);
    }
    
    private ZoneType(boolean isStrict)
    {
        this(true, isStrict);
    }
    
    private ZoneType(boolean straightOnly, boolean isStrict)
    {
        this(straightOnly, isStrict, null);
    }
    
    private ZoneType(boolean straightOnly, boolean isStrict, @Nullable CardPosition defaultCardPosition)
    {
        this.straightOnly = straightOnly;
        this.isStrict = isStrict;
        this.defaultCardPosition = defaultCardPosition;
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
