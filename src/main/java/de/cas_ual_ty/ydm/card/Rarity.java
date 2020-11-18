package de.cas_ual_ty.ydm.card;

public enum Rarity
{
    COMMON("Common"), SUPPLY("Supply");
    
    public final String name;
    
    private Rarity(String name)
    {
        this.name = name;
    }
    
    public static final Rarity[] VALUES = Rarity.values();
    
    public static Rarity fromString(String s)
    {
        for(Rarity rarity : Rarity.VALUES)
        {
            if(rarity.name.equals(s))
            {
                return rarity;
            }
        }
        
        return null;
    }
}
