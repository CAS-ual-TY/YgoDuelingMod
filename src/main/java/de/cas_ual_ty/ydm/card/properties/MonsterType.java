package de.cas_ual_ty.ydm.card.properties;

public enum MonsterType
{
    FUSION("Fusion", true), LINK("Link", true), RITUAL("Ritual"), SYNCHRO("Synchro", true), XYZ("Xyz", true);
    
    public final String name;
    public final boolean isExtraDeck;
    
    MonsterType(String name, boolean isExtraDeck)
    {
        this.name = name;
        this.isExtraDeck = isExtraDeck;
    }
    
    MonsterType(String name)
    {
        this(name, false);
    }
    
    public static final MonsterType[] VALUES = MonsterType.values();
    
    public static MonsterType fromString(String s)
    {
        for(MonsterType monsterType : MonsterType.VALUES)
        {
            if(monsterType.name.equals(s))
            {
                return monsterType;
            }
        }
        
        return null;
    }
}
