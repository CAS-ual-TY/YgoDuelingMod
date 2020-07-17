package de.cas_ual_ty.ydm.card.properties;

public enum Ability
{
    FLIP("Flip"), GEMINI("Gemini"), SPIRIT("Spirit"), TOON("Toon"), UNION("Union");
    
    public final String name;
    
    private Ability(String name)
    {
        this.name = name;
    }
    
    public static final Ability[] VALUES = Ability.values();
    
    public static Ability fromString(String s)
    {
        for(Ability ability : Ability.VALUES)
        {
            if(ability.name.equals(s))
            {
                return ability;
            }
        }
        
        return null;
    }
}
