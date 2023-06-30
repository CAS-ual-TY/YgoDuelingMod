package de.cas_ual_ty.ydm.rarity;

public enum Rarities
{
    COMMON("Common"), SUPPLY("Supply"), CREATIVE("Creative");
    
    public final String name;
    
    Rarities(String name)
    {
        this.name = name;
    }
}
