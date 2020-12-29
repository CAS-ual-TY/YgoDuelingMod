package de.cas_ual_ty.ydm.card;

public enum Rarity
{
    COMMON("Common"), SUPPLY("Supply");
    
    public final String name;
    
    private Rarity(String name)
    {
        this.name = name;
    }
}
