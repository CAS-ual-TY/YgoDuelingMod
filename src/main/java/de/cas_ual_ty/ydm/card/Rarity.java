package de.cas_ual_ty.ydm.card;

public enum Rarity
{
    COMMON("Common"), SUPPLY("Supply"), CREATIVE("Creative");
    
    public final String name;
    
    Rarity(String name)
    {
        this.name = name;
    }
}
